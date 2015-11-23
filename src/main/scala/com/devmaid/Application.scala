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

import java.io.File
import java.nio.file.Paths

import com.devmaid.common.config.Configuration
import com.devmaid.common.file.TimePrinter
import com.devmaid.common.file.ssh.SshManager
import com.devmaid.common.Log
import com.devmaid.common.Util
import com.devmaid.watch.FileWatcher

class Application(config: Configuration) extends Log {

  case class ConnectivityException(smth: String) extends Exception(smth)

  val filesArray = config.sourceRoots map {
    sourceRoot => Util.filesToLoad(sourceRoot, "", config.fileTypesToBeWatched, config.fileTypesToBeIgnored)
  }
  val uploaderWithTimePrinter = new SshManager(config) with TimePrinter
  
  val watcher = new FileWatcher(uploaderWithTimePrinter, config.sourceRoots, config.refreshIntervalInSeconds, 
      config.fileTypesToBeWatched, config.fileTypesToBeIgnored)
  
  def cleanUpAndExit() = {
    watcher.close()
  }
  
  def run(): Unit = {
    
    sys.addShutdownHook(cleanUpAndExit())
    
    testConnections()
    
    config.sourceRoots.zipWithIndex.foreach {
      case (_, i) => watcher.addToWatched(Paths.get(config.sourceRoots(i)), i)
    }
    filesArray.zipWithIndex.foreach {
      case (files, i) =>
        {
          files map {
            file => Paths.get(file)
          } foreach {
            path =>
              {
                watcher.addToWatched(path, i)
              }
          }
        }
    }
    //info("all files to be watched:" + path);
    watcher.run() //infinite loop
  }

  private def testConnections(): Unit = {
    val result = uploaderWithTimePrinter.ls("/", 0)
    info("In testConnections, result: " + result)
    if (result == null) {
      throw new ConnectivityException("Exception thrown");
    }
  }
}

