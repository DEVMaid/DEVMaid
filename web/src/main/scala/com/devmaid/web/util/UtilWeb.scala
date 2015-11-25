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

package com.devmaid.web.util

import java.io.File
import java.net.InetAddress
import java.io.InputStream

object UtilWeb extends Log{
  
  def instance = this
  
  def getName(s: String) : String = {
    val file = new File(s);
    file.getName
  }
  
  def getLocalHostName(): String = {
    val ip = InetAddress.getLocalHost()
    ip.getHostName
  }
  
  override def info(s: String) = {
    super.info(s)
  }
  
  override def debug(s: String) = {
    super.debug(s)
  }
  
  
  /*
   * 
   */
  def getLocalHostNameRep(): String = {
    System.getProperty("user.name") + "@" + getLocalHostName()
  }
  
  def readInputStreamIntoString(is: InputStream): String = {
    scala.io.Source.fromInputStream(is).mkString
  }
  
}