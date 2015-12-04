/*
Copyright (c) 2015 Ken Wu
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#
# -----------------------------------------------------------------------------
#
# Author: Ken Wu
# Date: 2015
* 
*/

package com.devmaid.common.file.ssh

import com.jcabi.ssh.Shell
import com.devmaid.common.Log
import com.devmaid.common.config.Configuration
import com.devmaid.common.file.{ FileSynchronizer, RemoteResult }
import com.devmaid.common.Util
import scala.io.Source
import java.io.IOException
import scala.sys.process._
/*
 * Each SshManager will process a single SSH connection to one host (although various sourceRoots and destination roots)
 */
class SshManager(val config: Configuration) extends FileSynchronizer with Log {

  private def createSSHClient() = {
    val ssh = SshManager.createSSH(config)
    new RichSshClient(ssh)
    //sshClient.exec("ls") //test a simple command to make sure it works
  }

  override def uploadFile(fileToLoad: String, sourceIndex: Int): RemoteResult = {
    val source = Util.joinPath(config.sourceRoots(sourceIndex), fileToLoad)
    val destination = Util.joinPath(config.destinationRoots(sourceIndex), fileToLoad)
    _updateRemoteFile(source, destination)
  }

  override def createDir(dirToCreate: String, sourceIndex: Int): RemoteResult = {
    return createDir(dirToCreate, sourceIndex, false)
  }

  def createDir(dirToCreate: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val destination = if (isAbsolutePath) dirToCreate else Util.joinPath(config.destinationRoots(sourceIndex), dirToCreate)
    _updateRemoteFile(null, destination)
  }

  override def removeFile(file: String, sourceIndex: Int): RemoteResult = {
    return removeFile(file, sourceIndex, false)
  }

  def removeFile(file: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val fullPFilePath = if (isAbsolutePath) file else Util.joinPath(config.destinationRoots(sourceIndex), file)
    _exec("rm -rf " + fullPFilePath)
  }

  override def removeDir(file: String, sourceIndex: Int): RemoteResult = {
    return removeFile(file, sourceIndex, false)
  }

  def removeDir(file: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    return removeFile(file, sourceIndex, isAbsolutePath)
  }

  /*
   * if source is null, then just create the dir only
   */
  private def _updateRemoteFile(source: String, destination: String): RemoteResult = {
    val sshClient = createSSHClient()
    info("In _updateRemoteFile, source: " + source + ", destination: " + destination)
    var res = sshClient.recursivelyCreatePathIfNotExists(destination, source != null)
    if (source != null) {
      try {
        val result = sshClient.upload(source, destination)
        res = RemoteResult(RemoteResult.SUCESS, Some(result))
      } catch {
        case ioe: Exception => {
          debug("In _updateRemoteFile, ERROR -> source: " + source + ", destination: " + destination + " has IOException error: " + ioe)
          return RemoteResult(RemoteResult.ERROR, Some(ioe.toString))
        }
      }
    }
    return res
  }

  /*
   * Be careful to call this method! it is very dangerous
   */
  def removeRoot(sourceIndex: Int): RemoteResult = {
    _exec("rm -rf " + config.destinationRoots(sourceIndex))
  }

  def cat(file: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val fullPFilePath = if (isAbsolutePath) file else Util.joinPath(config.destinationRoots(sourceIndex), file)
    _exec("cat " + fullPFilePath)
  }

  def find(rootFolder: String, keyword: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val fullPFilePath = if (isAbsolutePath) rootFolder else Util.joinPath(config.destinationRoots(sourceIndex), rootFolder)
    val fullPFilePathWithOutSlashAtTheEnd = if ((fullPFilePath takeRight 1) == "/") fullPFilePath take fullPFilePath.length - 1 else fullPFilePath
    _exec("find " + fullPFilePathWithOutSlashAtTheEnd + " -iname '" + keyword + "' -exec ls -laF {} \\;")
  }

  def ls(file: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val fullPFilePath = if (isAbsolutePath) file else Util.joinPath(config.destinationRoots(sourceIndex), file)
    _exec("ls -laF " + fullPFilePath)
  }

  def write(file: String, content: String, sourceIndex: Int, isAbsolutePath: Boolean = false): RemoteResult = {
    val fullPFilePath = if (isAbsolutePath) file else Util.joinPath(config.destinationRoots(sourceIndex), file)
    _exec("echo \"" + content + "\" > " + file + " ")
  }
  
  def exec(currentWorkingDir: String, commandToBeExecuted: String): RemoteResult = {
    return _exec(commandToBeExecuted, true, currentWorkingDir)
  }
  
  private def _exec(command: String, retrieveWorkingDir: Boolean = false, currentWorkingDir: String = ""): RemoteResult = {
    val sshClient = createSSHClient()
    val r = retrieveWorkingDir match {
      case true => sshClient.execAndRetrieveWorkingDir(command, currentWorkingDir)
      case false => sshClient.exec(command)
    }
    debug("In _exec, command: " + snapshot(command) + ", result-status:" + r.status + ", result-message:" + r.message.getOrElse("None") + ", result-resultWorkingDir:"+r.resultWorkingDir.getOrElse("None"))
    return r
  }

  def exists(file: String, isThisAFile: Boolean, sourceIndex: Int): Boolean = {
    val lsStr = ls(file, sourceIndex)
    lsStr.message match {
      case Some(x) => {
        val lowerX = x.toLowerCase()
        val fileNotFound = lowerX.contains("no such file or directory")
        if (isThisAFile) {
          if (fileNotFound) {
            false
          } else {
            x.indexOfSlice(file) > -1
          }
        } else {
          val emptyResponse = lowerX.length() == 0
          debug("In exists, lowerX: " + lowerX + ", emptyResponse:" + emptyResponse + ", fileNotFound:" + fileNotFound)
          emptyResponse || !fileNotFound
        }
      }
      case None => false
    }
  }

  /*
   * mode: 
   *    -1 means copying from local host to remote
   *    0 means copying from remote to remote
   *    1 means copying from remote to local
   */
  def scp(fSource: String, fDest: String, mode: Int = 0): Boolean = {
    val sshClient = createSSHClient()
    val sshKeyFile = sshClient.keyfile
    val connectionString = sshClient.login + "@" + sshClient.hostname + ":"
    val scpCommand = mode match {
      case 0 | 1 => 
        Seq("scp", "-i", sshKeyFile,  connectionString  + fSource, fDest)
      case -1 => 
        Seq("scp", "-i", sshKeyFile, fSource, connectionString + fDest)
    }

    //Now make sure the fDest parent directory exists
    if (mode == 0 || mode == 1) {
      Util.ensureParentDir(fDest)
    }
    info("scp command executing: " + scpCommand.toString)
    return scpCommand.! == 0
  }

}

object SshManager extends Log {
  def createSSH(config: Configuration): SSHDaemon = {
    createSSH(config.connection.hostname, config.connection.login, config.connection.keyfile)
  }

  private def createSSH(hostname: String, login: String, keyfile: String): SSHDaemon = {
    info("hostname: " + hostname + ", login: " + login)
    val fixedKeyFile = Util.translateUserHomeDirIfThereIsOne(keyfile);
    val keyContent = Source.fromFile(fixedKeyFile).getLines.mkString("\n")
    val ssh = new SSHDaemon(
      hostname, 22,
      login, fixedKeyFile, keyContent)
    ssh
  }
}
