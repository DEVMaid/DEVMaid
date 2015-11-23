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
import com.devmaid.common.file.ssh.SshManager
import com.devmaid.web.JettyLauncher

object SshClient extends Log {

  val sshManagers = (for (i <- 0 to JettyLauncher.configFiles.size - 1) yield new SshManager(JettyLauncher.getConfig(i))).toList

  def find(rootFolder: String, keyword: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).find(rootFolder, keyword, sourceIndex, isAbsolutePath)
    result.message.getOrElse("")
  }

  def ls(file: String, connectionIndex: Int, sourceIndex: Int, isAbsolutePath: Boolean = false): String = {
    val result = sshManagers(connectionIndex).ls(file, sourceIndex, isAbsolutePath)
    result.message.getOrElse("")
  }

}