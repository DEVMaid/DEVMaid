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

import org.scalatest.Tag
import org.scalatest.{ BeforeAndAfter, Matchers, FunSpec }
import com.devmaid.common.Util
import com.devmaid.common.Log
import com.devmaid.TestUtil._

class ApplicationTest extends FunSpec with BaseSpec with Matchers with BeforeAndAfter with Log {

  //"Runs the stimulation in the sperate thread while having the main thread as the house keeper" 

  reset(configuration.sourceRoots, "", true)
  resetRemote()

  //Now turn on the server synchronizer to start the server synchronization
  val appToRun = new Thread(new Runnable {
    def run() {
      val app = new Application(configuration)
      app.run()
    }
  })
  appToRun.start

  val devmaidStartUpTimeInSeconds = 2
  //Sleep double amount of time to give enough time for the application to start up properly
  Thread.sleep(devmaidStartUpTimeInSeconds * 1000)

  val testRefreshIntervalInSeconds = configuration.refreshIntervalInSeconds * 7

  it("This test the directory creation logic", Tag("DirectoryCreationAndDeletion")) {
    val sDirName = generateDirName()
    //Make sure this fake directory does not exist remotely
    assert(!sshManager.exists(sDirName, isThisAFile = false, 0))

    info("creating sDirName:" + sDirName)
    initTestAtSource0.createADir(sDirName)
    //Make sure this exists locally first
    assert(initTestAtSource0.exists(sDirName))

    //Sleep enough time for the remote synchronization
    Thread.sleep(testRefreshIntervalInSeconds * 1000)

    //Now make sure the remote directory exists
    assert(sshManager.exists(sDirName, isThisAFile = false, 0))
    
    createAFileAndMakeSureItIsThere(sDirName)
    
    
    info("deleting sDirName:" + sDirName)
    initTestAtSource0.removeADir(sDirName)
    //Make sure this got removed locally first
    assert(!initTestAtSource0.exists(sDirName))
    
    //Sleep enough time for the remote synchronization
    Thread.sleep(testRefreshIntervalInSeconds * 1000)

    //Now make sure the remote file does not
    assert(!sshManager.exists(sDirName, isThisAFile = false, 0))
    

  }

  it("This test the file creation and deletion logic", Tag("FileCreationAndDeletion")) {
    val fileCreated = createAFileAndMakeSureItIsThere()
    deleteAFileAndMakeSureItIsDeleted(fileCreated)
    
  }

  private def createAFileAndMakeSureItIsThere(onParentDir: String = ""): String = {
    //Create a fake local file from source
    val sfileName = Util.joinPath(onParentDir, generateFileName())
    //Make sure this fake file does not exist remotely
    assert(!sshManager.exists(sfileName, isThisAFile = true, 0))

    info("creating sfileName:" + sfileName)
    initTestAtSource0.createAFileAndWriteContentToIt(sfileName, "hello This is Ken")

    //Sleep enough time for the remote synchronization
    Thread.sleep(testRefreshIntervalInSeconds * 1000)

    //Now make sure the remote file exists
    assert(sshManager.exists(sfileName, isThisAFile = true, 0))
    sfileName
  }
  
  private def deleteAFileAndMakeSureItIsDeleted(sfileName: String): Unit = {
    assert(sshManager.exists(sfileName, isThisAFile = true, 0))
    info("removing sfileName:" + sfileName)
    initTestAtSource0.removeAFile(sfileName)
    
    //Sleep enough time for the remote synchronization
    Thread.sleep(testRefreshIntervalInSeconds * 1000)
    
    //Now make sure the file is removed remotely
    assert(!sshManager.exists(sfileName, isThisAFile = true, 0))
  }

}
