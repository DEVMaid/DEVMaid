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

package com.devmaid.web.remotefs

import com.devmaid.TestUtil._
import com.devmaid.BaseSpec
import com.devmaid.common.Log
import com.devmaid.common.Util
import com.devmaid.web.ssh.SshClient
import org.scalatest.{ FunSpec, Matchers }
import org.scalatest.Tag


class RemoteFileTest extends FunSpec with BaseSpec with Matchers with Log {

  val TEST_RESOURCES_BASE = List[String]("/tmp/devmaid/RemoteFileTest")
  
  //This code is to create a sshClient test connection
  val modifiedConfigurations = BaseSpec.modifiyConfigurationsWithCurrentUserAccount(configurations)
  SshClient.setConfigFiles(modifiedConfigurations)
  
  it("test constructing a remote file with basic functionalities like listFiles() and find()", Tag("RemoteFileTest1_Basic")) {
    val FILE1 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/testa.txt")
    val FILE2 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/b/testb.txt")
    val FILE3 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/c/testc.txt")
    val FILE3_1 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/c/tesc.txt")
    reset(TEST_RESOURCES_BASE, "", true)
    Util.filesToLoad(TEST_RESOURCES_BASE(0)) should not contain allOf(FILE1, FILE2, FILE3)
    writeToFile(FILE1, "Content-" + FILE1)
    writeToFile(FILE2, "Content-" + FILE2)
    writeToFile(FILE3, "Content-" + FILE3)
    writeToFile(FILE3_1, "Content-" + FILE3_1)
    
    val rf = new RemoteFile(TEST_RESOURCES_BASE(0))
    
    val rf_testc = rf.find("testc.txt")
    assert(rf_testc.size == 1 && rf_testc(0).getFile==FILE3)
    
    val rf_test_star = rf.find("test*.txt")
    
    assert(rf_test_star.size==3)
    
  }

}
