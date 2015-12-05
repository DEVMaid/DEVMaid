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

package com.devmaid.web.ssh

import com.devmaid.TestUtil._
import com.devmaid.BaseSpec
import com.devmaid.common.Log
import com.devmaid.common.Util
import org.scalatest.{ FunSpec, Matchers }
import org.scalatest.Tag
import com.devmaid.web.data.TerminalResponse;

class SshClientTest extends FunSpec with BaseSpec with Matchers with Log {

  val TEST_RESOURCES_BASE = List[String]("/tmp/devmaid/SshClientTest")

  //This code is to create a sshClient test connection
  val modifiedConfigurations = BaseSpec.modifiyConfigurationsWithCurrentUserAccount(configurations)
  SshClient.setConfigFiles(modifiedConfigurations)

  it("testing the exec method", Tag("SshClientTest_exec")) {
    reset(TEST_RESOURCES_BASE, "", true)
    val curDir = Util.joinPath(TEST_RESOURCES_BASE(0), "currentDir/")
    val parentDir = Util.joinPath(TEST_RESOURCES_BASE(0), "")
    val msg = "going back to parent"
    val command = "echo '"+msg+"' && cd .. "
    val connectionIndex=0
    val tRFail = SshClient.exec(curDir, command, connectionIndex);

    //It should throw an error first because the curDir does not exist
    assert(!tRFail.sucess)
    
    ensureDir(curDir)  //Now create the directory and re-execing the previous command
    val tRSucess = SshClient.exec(curDir, command, connectionIndex);
    assert(tRSucess.sucess)
    assert(tRSucess.getOutput==msg)
    assert(areTwoPathsTheSame(tRSucess.getResultWorkingDir, parentDir))
    
  }

}
