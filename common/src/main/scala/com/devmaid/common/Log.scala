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

package com.devmaid.common

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

trait Log {
  private[this] val logger = LoggerFactory.getLogger(getClass().getSimpleName());
  
  def gclass() = {
    getClass().getName()
  }
  
  
  def info(message:String) : Unit = {
    _p("[INFO]", message, null)
  }
  
  def debug(message:String) : Unit = {
    _p("[DEBUG]", message, null)
  }
  
  def snapshot(s: String): String = {
    return if(s.length<100) s else ((s take 50) + " ... " + (s takeRight 50))
  }
  
  private def _p(messageType:String, message:String) : Unit = {
    _p(messageType, message, null)
  }
  private def _p(messageType:String, message:String, ex:Throwable) : Unit = {
    if (ex == null) {
      println(messageType + " " + gclass + " " + message)  
    } else {
      println(messageType + " " + gclass + " " + message + ".  ex:"+ex)
    }
  }

}
