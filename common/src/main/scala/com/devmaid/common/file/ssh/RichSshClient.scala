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
import com.jcabi.ssh.SSH
import java.io.File
import scala.language.implicitConversions
import com.devmaid.common.Log
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException

class RichSshClient(val ssh: SSH) extends Log {

  val plainClient = new Shell.Plain(ssh)

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

  def exec(command: String): String =
    plainClient.exec(command)
    
    
  private def getParentPath(path: String) =
    if (path.contains('/')) path.substring(0, path.lastIndexOf('/')) else path
}

