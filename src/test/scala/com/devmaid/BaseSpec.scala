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

import com.devmaid.common.config.Configuration
import com.devmaid.common.config.Connection
import org.scalatest.{ BeforeAndAfter, Matchers, WordSpec, FlatSpec }
import com.devmaid.common.file.ssh.SshManager
import com.devmaid.common.Util
import com.devmaid.common.Log
import com.devmaid.TestUtil._
import java.io.File

trait BaseSpec extends Log {

  val configurationPath = BaseSpec.configurationPath
  val configurations = BaseSpec.configurations
  val configuration = BaseSpec.configuration
  //val configuration = Configuration.load("./src/test/resources/test-config-iq-spark.json")
  val sshManager = BaseSpec.createSSHManager(configuration)

  class InitTest(val conf: Configuration, sourceIndex: Int) {
    def createAFileAndWriteContentToIt(f: String, c: String): Unit = {
      val sfileName = f
      val sfilePath = Util.joinPath(configuration.sourceRoots(sourceIndex), sfileName)
      debug("createAFileAndWriteContentToIt sfilePath: " + sfilePath)
      TestUtil.writeToFile(sfilePath, c)
    }
    def createADir(d: String): Unit = {
      val sfileName = d
      val sfilePath = Util.joinPath(configuration.sourceRoots(sourceIndex), d)
      val created = TestUtil.createDir(sfilePath)
      debug("createADir sfilePath: " + sfilePath + ", created: " + created)
    }
    def exists(d: String): Boolean = {
      val sfileName = d
      val sfilePath = Util.joinPath(configuration.sourceRoots(sourceIndex), d)
      TestUtil.dirExists(sfilePath)
    }
    def removeADir(f: String): Boolean = {
      val sDirPath = Util.joinPath(configuration.sourceRoots(sourceIndex), f)
      TestUtil.deleteDir(sDirPath)
    }
    def removeAFile(f: String): Boolean = {
      val sfilePath = Util.joinPath(configuration.sourceRoots(sourceIndex), f)
      TestUtil.deleteFile(sfilePath)
    }
  }

  val initTestAtSource0 = createInitTest(0)

  def createInitTest(sourceIndex: Int) = {
    new InitTest(configuration, sourceIndex)
  }

  def reset(baseDirs: List[String], thisDir: String, create: Boolean): Unit = {
    baseDirs.zipWithIndex.foreach {
      case (baseDir, i) =>
        {
          val p = Util.joinPath(baseDir, thisDir)
          deleteDir(p)
          if (create) {
            new File(Util.joinPath(baseDir, thisDir)).mkdirs()
          }
        }
    }
  }

  def resetRemote(): Unit = {
    configuration.sourceRoots.zipWithIndex.foreach {
      case(_, i) => sshManager.removeRoot(i)
    }
  }

  def generateFileName(extenstion: String = "txt"): String = {
    getRunningClassName + "-test-" + BaseSpec.inc + "." + extenstion
  }

  def generateDirName(parentDir: String = ""): String = {
    Util.joinPath(parentDir, "dir-" + getRunningClassName)
  }

  private def getRunningClassName(): String = {
    getClass().getSimpleName()
  }
}

object BaseSpec extends Log {
  val configurationPath = "./src/test/resources/test-config-localhost.json"
  val configurations = Some(Configuration.load(configurationPath))
  var configuration = configurations.get(0)
  private var current = 0
  private def inc = { current += 1; current }
  val sshManager = try { // Make it singleton for more efficient
    val sm = new SshManager(configuration)
    sm.ls("", 0) //Make sure it is working by doing simple commands
    sm
  } catch {
    case ioe: java.io.IOException => {
      val newConfiguration = modifiyConfigurationsWithCurrentUserAccount(configurations).get(0)
      val sm = new SshManager(newConfiguration)
      //sm.ls("", 0) //Make sure it is working by doing simple commands
      configuration = newConfiguration  //Reassign it back
      sm
    }
  }

  //This method modify the original configurations for any bad connection replaced with the current user account
  def modifiyConfigurationsWithCurrentUserAccount(origConfigurations: Option[List[Configuration]]): Option[List[Configuration]] = {
    val user = System.getenv("USER")
    val newConfigurations = (for (i <- 0 to origConfigurations.size - 1) 
      yield new Configuration(new Connection(origConfigurations.get(i).connection.hostname, user, origConfigurations.get(i).connection.keyfile), configuration.sourceRoots, configuration.destinationRoots,
        origConfigurations.get(i).refreshIntervalInSeconds, origConfigurations.get(i).fileTypesToBeWatched, origConfigurations.get(i).fileTypesToBeIgnored)
    ) 
    return Some(newConfigurations.toList)
  }
  
  def createSSHManager(configuration: Configuration): SshManager = {
    sshManager
  }
}
