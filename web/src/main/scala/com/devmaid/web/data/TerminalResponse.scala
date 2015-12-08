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

package com.devmaid.web.data

/*
*	sucess: represents if a terminal command sucesses or not
	output: contains all the original terminal output
	resultWorkingDir: indiciates the current Working director after a command is executed
			For example, if this command is executed: cd /tmp/ && echo 'hello' && cd /etc/DEVMaid
			resultWorkingDir should indiciate -> /etc/DEVMaid
*/
case class TerminalResponse(sucess: Boolean, output: Option[String], resultWorkingDir: Option[String], directoryItems: Option[List[(Boolean, String)]] = None) {
  def getOutput = output.getOrElse(TerminalResponse.EMPTY_RESPONSE_STR)
  def getResultWorkingDir = resultWorkingDir.getOrElse(TerminalResponse.EMPTY_RESPONSE_STR)
  def getDirectoryItems = directoryItems match {
    case None => TerminalResponse.EMPTY_RESPONSE_STR
    case _ => {
      (for(i <- 0 to directoryItems.get.size-1) yield directoryItems.get(i)._2).toList.mkString(",") 
    }
  }
  override def toString: String = {
    "->sucess = " + sucess + output.map(s => "; output = " + s).getOrElse("") + resultWorkingDir.map(s => "; resultWorkingDir = " + s).getOrElse("")+ directoryItems.map(s => "; directoryItems = " + s).getOrElse("")
  }
}

object TerminalResponse {
  val EMPTY_RESPONSE_STR = "*NONE*"
}