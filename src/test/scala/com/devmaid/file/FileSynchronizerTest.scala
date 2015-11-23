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

package com.devmaid.file

import org.scalatest.{ FlatSpec, Matchers,WordSpec }
import com.devmaid.BaseSpec
import com.devmaid.common.file.ssh.SshManager
import com.devmaid.common.config.Configuration
import sys.process._
import com.devmaid.common.Util
import com.devmaid.TestUtil
import org.scalatest.Assertions._

class FileSynchronizerTest extends WordSpec with BaseSpec with Matchers {

  "FileSynchronizerTest" when {
    "test the file uploader" should {
      
      val sfileName = generateFileName()
      
      //Make sure the test file is not there
      sshManager.removeFile(sfileName, 0)
      assert(!sshManager.exists(sfileName, isThisAFile = true, 0))

      //Create a fake local file from source
      initTestAtSource0.createAFileAndWriteContentToIt(sfileName, "hello This is Ken")

      //upload it to a test ssh server
      val response = sshManager.uploadFile(sfileName, 0)

      //Now file is uploaded, make sure it exists
      assert(sshManager.exists(sfileName, isThisAFile = true, 0))
    }
  }

}

