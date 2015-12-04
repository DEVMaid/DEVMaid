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
import org.json.JSONObject
import org.json.JSONException
import com.devmaid.common.Util;
import com.devmaid.web.data.TerminalRequest;

object UtilWeb extends Log {

  def instance = this

  def getName(s: String): String = {
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

  override def error(s: String) = {
    super.error(s)
  }

  def processTerminalRequestJson(jsonStr: String): Option[TerminalRequest] = {
    try {
      if(Util.isEmpty(jsonStr))
        return None;
      val jsonObject = new JSONObject(jsonStr);
      if (jsonObject.getString("method") == "terminal") {
        var results = jsonObject.getString("params");
        results = results.replaceAll("[\\[\\]]", ""); //replace all square brackets
        val resArr = results.split(",")
        return Some(new TerminalRequest(resArr(0), resArr(1)));
      } else {
        error("invalid json object field - method: " + jsonObject.getString("method"));
        return None;
      }
    } catch {
      case e: JSONException => {
        error("invalid json object : " + e.toString() + " on jsonStr: " + jsonStr);
        return None
      }
    }
  }

  /*
   * 
   */
  def getLocalHostNameRep(): String = {
    getHostNameRep()
  }

  def getUserHome(): String = {
    return System.getProperty("user.home")
  }
  
  def getHostNameRep(userName: String = "", hostName: String = ""): String = {
    val uN = Util.isEmpty(userName) match {
      case true => System.getProperty("user.name")
      case false => userName
    }
    val h = Util.isEmpty(hostName) match {
      case true => getLocalHostName()
      case false => hostName
    }
    uN + "@" + h
  }

  def getHostPathNameRep(userName: String = "", hostName: String = "", path: String = ""): String = {
    val p = Util.isEmpty(path) match {
      case true => "~/"
      case false => path
    }
     getHostNameRep(userName, hostName)+ ":" + p
  }
  
  def beautifyPath(path: String, homeDir: String = getUserHome): String = {
    var rPath = path.replace(homeDir, "~/")
    rPath=rPath.replace("//", "/")
    if((rPath takeRight 1)=="/") rPath take rPath.length -1 else rPath
  }

  def readInputStreamIntoString(is: InputStream): String = {
    scala.io.Source.fromInputStream(is).mkString
  }

}