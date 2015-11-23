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

package com.devmaid.common.config

import spray.json.JsonParser
import com.devmaid.common.Util
import com.devmaid.common.Log

case class InvalidConfigurationFormatException(smth: String) extends Exception(smth)

case class Configuration(
    connection: Connection,
    sourceRoots: List[String],
    destinationRoots: List[String],
    refreshIntervalInSeconds: Int,
    fileTypesToBeWatched: List[String],
    fileTypesToBeIgnored: List[String] = List[String]()) extends Product {
}

case class RawConfiguration(
    connections: List[Connection],
    sourceRoots: List[String],
    destinationRoots: List[String],
    refreshIntervalInSeconds: Int,
    fileTypesToBeWatched: List[String],
    fileTypesToBeIgnored: List[String] = List[String]()) extends Product {
}

object Configuration extends Log {

  def loadRaw(configFileName: String = "config.json"): RawConfiguration = {
    import com.devmaid.common.config.MyJsonProtocol._
    val configFile = loadConfigFile(configFileName)
    val rawConfig = JsonParser(configFile).convertTo[RawConfiguration]
    rawConfig
  }

  def load(configFileName: String = "config.json"): List[Configuration] = {
    val rawConfig = loadRaw(configFileName)
    val configs = internalConvertTheRawConfigurationToConfiguration(rawConfig)
    configs
  }

  private def containsOverlapping(dirs1: List[String]): Option[String] = {
    dirs1.foreach {
      x =>
        {
          dirs1.foreach {
            y =>
              {
                if (x != y) {
                  if (x.contains(y)) {
                    //debug("dewww x:" + x + ", y:" + y)
                    return Some("Dir: " + y + " contains dir: " + x + " already which is overlapping...")
                  } else if (y.contains(x)) {
                    //debug("dewww y:" + y + ", x:" + x)
                    return Some("Dir: " + x + " contains dir: " + y + " already which is overlapping...")
                  }
                }
              }
          }
        }
    }
    return None
  }

  private def internalConvertTheRawConfigurationToConfiguration(rawConfig: RawConfiguration): List[Configuration] = {
    if (rawConfig.sourceRoots.size != rawConfig.destinationRoots.size) {
      throw new InvalidConfigurationFormatException("sourceRoots size is not equal to destinationRoots size in the configuration file")
    }

    containsOverlapping(rawConfig.sourceRoots) match {
      case Some(x) => {
        throw new InvalidConfigurationFormatException("some of the source folder(s) are overlapping with others")
      }
      case None => {

      }
    }

    val cs = rawConfig.connections map {
      c =>
        new Configuration(c, rawConfig.sourceRoots, rawConfig.destinationRoots,
          rawConfig.refreshIntervalInSeconds, rawConfig.fileTypesToBeWatched, rawConfig.fileTypesToBeIgnored)
    }
    cs
  }

  private def loadConfigFile(configFileName: String): String = {
    val newConfigFileName = Util.translateUserHomeDirIfThereIsOne(configFileName)
    scala.io.Source.fromFile(newConfigFileName).getLines().mkString
  }

  def searchForSourceIndex(sourceRoots: List[String], path: String): Int = {
    var foundIndex = -1
    sourceRoots.zipWithIndex.foreach {
      case (_, i) => {
        if (path.contains(sourceRoots(i)))
          foundIndex = i
      }
    }
    foundIndex
  }
}
