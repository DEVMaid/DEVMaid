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

package com.devmaid

import java.nio.file.{ Path, Paths, Files }
import java.io._
import org.apache.commons.io.FileUtils

import com.devmaid.common.Log

object TestUtil extends Log {

  def deleteDir(p: String): Boolean = {
    debug("now deleting directory: " + p)
    try {
      FileUtils.deleteDirectory(new File(p));
      true
    } catch {
      case e: IOException => {
        error("There is problem when deleting a directory: " + p)
      }
    }
    false
  }

  def deleteFile(f: String): Boolean = {
    debug("now deleting file: " + f)
    FileUtils.deleteQuietly(new File(f));
  }

  def dirExists(p: String): Boolean = {
    return Files.exists(Paths.get(p))
  }
  
  def fileExists(p: String): Boolean = {
    val f = new File(p)
    return f.exists()
  }

  // Return false if it already exists 
  // true if otherwise when trying to create
  def ensureDir(p: String): Boolean = {
    if (dirExists(p)) {
      false
    } else {
      createDir(p)
    }
  }

  def createDir(p: String): Boolean = {
    val f = new File(p)
    if (!f.exists()) {
      debug("In createDir, directory to be created: " + p)
      val res = f.mkdirs()
      debug("In createDir, directory : " + p + " created: " + res)
      res
    } else {
      debug("In createDir, directory exists! - " + p)
      false
    }
  }

  def getDir(p: String): String = {
    val file = new java.io.File(p)
    file.getParentFile.toString
  }

  def writeToFile(p: String, c: String): Unit = {
    val dp = getDir(p)
    ensureDir(dp)
    val pw = new PrintWriter(new File(p))
    pw.write(c)
    pw.close
  }

  def areTwoPathsTheSame(p1: String, p2: String): Boolean = {
    def r(p:String): String = p takeRight 1
    if(r(p1)=="/") {
      if(r(p2)=="/") {
        return p1 == p2
      } else {
        return p1 == (p2+"/")
      }
    } else {
      if(r(p2)=="/") {
        return p1 == (p2 take p2.length-1)
      } else {
        return p1 == p2
      }
    }
  }
  
}

