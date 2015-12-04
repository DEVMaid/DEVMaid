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

import java.util.logging.Level;
import com.jcabi.log.Logger;
import com.jcabi.ssh.Shell
import java.io.File
import scala.language.implicitConversions
import com.devmaid.common.Log
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import org.apache.commons.io.input.NullInputStream;
import com.devmaid.common.file.RemoteResult

class RichSshClient(val sshDaemon: SSHDaemon) extends Log {

  val ssh = sshDaemon.getSSH
  val hostname = sshDaemon.hostname
  val login = sshDaemon.login
  val keyfile = sshDaemon.keyfile
  val plainClient = new Shell.Plain(ssh)
  val verboseClient = new Shell.Verbose(ssh)
  val output = new ByteArrayOutputStream();
  val errorOutput = new ByteArrayOutputStream();

  /*
   * path: the path to be create
   * createOnParentPath: true means create on the parent path otherwise itself
   */
  def recursivelyCreatePathIfNotExists(path: String, createOnParentPath: Boolean = false) = {
    val command = "mkdir -p " + (if (createOnParentPath == true) getParentPath(path) else path)
    debug("In recursivelyCreatePathIfNotExists, command: " + command)
    exec(command)
  }

  def upload(s: String, d: String): String = {
    var errStr = ""
    try {
      val file = new File(s);
      val resOut = new ByteArrayOutputStream();
      val errOut = new ByteArrayOutputStream();
      val response = new Shell.Safe(ssh).exec(
        "cat > " + d + "",
        new FileInputStream(file),
        resOut,
        errOut);
      //debug("In upload, res: " + resOut)
      return resOut.toString
    } catch {
      case iae: Exception => {
        val errStr = "Errors in upload..."
        //debug("In upload, ERROR -> source: " + s + ", destination: " + d + " has error: " + errStr)
        throw new IOException(errStr)
      }
    }
  }

  def execAndRetrieveWorkingDir(command: String, curWorkingDir: String): RemoteResult = {
      def stripFrontAndRear(s: String) : String = {
        return s.substring(1, s.length()-1)
      }
      val sCommand = stripFrontAndRear(command)
      val sCurWorkingDir = stripFrontAndRear(curWorkingDir)
      val finalWholeCommand = ((new StringBuilder("cd ")).append(sCurWorkingDir).append(" && ").append(sCommand).append(" && pwd")).toString 
      val execResult = execNonPlain(finalWholeCommand)  //Append the getting current directory command at the end
      debug("In execAndRetrieveWorkingDir, finalWholeCommand: "+finalWholeCommand+", execResult:" + execResult + ", execResult._2:" + execResult._2 + ", lastIndexOfNewLine:" + execResult._2.lastIndexOf("\n"))
      val remoteResult = execResult._1 match {
        case 0 => {
          val rawContents = execResult._2.substring(0, execResult._2.length()-1)  //This trim out a new line character at the end
          debug("In execAndRetrieveWorkingDir, rawContents:" + rawContents)
          val contents = rawContents.substring(0, (rawContents take rawContents.length-2).lastIndexOf("\n"));
          val resultedWorkingDir = rawContents.substring(rawContents.lastIndexOf("\n")+1);
         new RemoteResult(RemoteResult.SUCESS, Some(contents), Some(resultedWorkingDir)) 
        }
        case _ => new RemoteResult(RemoteResult.ERROR, Some(execResult._2), None)
      }
      return remoteResult
  }

  def execNonPlain(command: String): (Int, String) = {
    val exitCode = verboseClient.exec(command, new NullInputStream(0L), output, errorOutput)
    (exitCode, exitCode match {
      case 0 => output.toString
      case _ => errorOutput.toString
    })
  }

  def exec(command: String): RemoteResult = {
    val msg = plainClient.exec(command)
    return new RemoteResult(RemoteResult.SUCESS, Some(msg), None)
  }

  private def getParentPath(path: String) =
    if (path.contains('/')) path.substring(0, path.lastIndexOf('/')) else path
}

