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

//This class is serving a ssh client for the web package to connect to all remote machines defined by the json configuration file 
// (default should be located in /etc/DEVMaid/json.config) 

package com.devmaid.web.ssh

import com.devmaid.web.util.Log
import com.devmaid.common.config.Configuration
import com.devmaid.common.file.ssh.SshManager
import com.devmaid.common.Util
import com.devmaid.web.JettyLauncher
import com.devmaid.web.data.TerminalResponse;

object SshClient extends Log {
  var configFiles = None: Option[List[Configuration]] //This needs to be externally set

  lazy val sshManagers = (for (i <- 0 to getConfigFiles.size - 1) yield new SshManager(getConfigFiles.get(i))).toList

  def setConfigFiles(cfs: Option[List[Configuration]]) = {
    configFiles = cfs
  }
  def getConfigFiles() = configFiles

  def cat(file: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).cat(file, sourceIndex, isAbsolutePath)
    result.message.getOrElse("").stripLineEnd
  }

  def find(rootFolder: String, keyword: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).find(rootFolder, keyword, sourceIndex, isAbsolutePath)
    result.message.getOrElse("")
  }

  def ls(file: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).ls(file, sourceIndex, isAbsolutePath)
    result.message.getOrElse("")
  }

  def scp(fSource: String, fDest: String,connectionIndex: Int): Boolean = {
    return sshManagers(connectionIndex).scp(fSource, fDest, 0)
  }
  
  def scpFrom(fSource: String, fDest: String,connectionIndex: Int): Boolean = {
    return sshManagers(connectionIndex).scp(fSource, fDest, -1)
  }
  
  def write(file: String, content: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).write(file, content, sourceIndex, isAbsolutePath)
    result.message.getOrElse("")
  }
  
  /*
   * Executes an arbitrary command on a working Directory and then return a TerminalResponse object
   */
  def exec(currentWorkingDir: String, command: String, connectionIndex: Int): TerminalResponse = {
    val tR = Util.isEmpty(command) match {
      case false => {
        val result = sshManagers(connectionIndex).exec(currentWorkingDir, command)
        new TerminalResponse(result.isSucess(), result.message, result.resultWorkingDir, result.directoryItems)
      }
      case true => new TerminalResponse(true, Some(""), Some(""), None)
    } 
    return tR
  }

}