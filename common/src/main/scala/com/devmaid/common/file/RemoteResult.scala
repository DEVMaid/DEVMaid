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

package com.devmaid.common.file

case class RemoteResult(status: String, message: Option[String] = None, resultWorkingDir: Option[String] = None){
  override def toString: String = {
    "->status = " + status + message.map(s => "; message = " + s).getOrElse("") + resultWorkingDir.map(s => "; resultWorkingDir = " + s).getOrElse("")
  }
  
  def isSucess() : Boolean = {
    status == RemoteResult.SUCESS
  }
}

object RemoteResult {
  val SUCESS = "sucess"
  val ERROR = "error"
}
