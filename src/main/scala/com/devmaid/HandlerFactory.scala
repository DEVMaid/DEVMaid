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

import com.devmaid.common.file.FileSynchronizer
import com.devmaid.common.file.RemoteResult

class HandlerFactory(val fileSynch: FileSynchronizer) {

  object UploadOperation extends Enumeration {
    type UploadType = Value
    val upload, remove= Value
  }
  
  def getHandlerForFile(file: String, operation:UploadOperation.UploadType, sourceIndex: Int) = () => {
    if (operation == UploadOperation.upload) {
      _exec(file, sourceIndex, fileSynch.uploadFile)
    } else if (operation == UploadOperation.remove){
      _exec(file, sourceIndex, fileSynch.removeFile)
    } else {
      throw new IllegalArgumentException("Unknown operation: " + operation);
    }
  }
  def getHandlerForDir(file: String, operation:UploadOperation.UploadType, sourceIndex: Int) = () => {
    if (operation == UploadOperation.upload) {
      _exec(file, sourceIndex, fileSynch.createDir)
    } else if (operation == UploadOperation.remove){
      _exec(file, sourceIndex, fileSynch.removeDir)
    } else {
      throw new IllegalArgumentException("Unknown operation: " + operation);
    }
  }
  
  private def _exec (file: String, sourceIndex: Int, f: (String, Int) => RemoteResult): RemoteResult = {
    f(file, sourceIndex)
  }

}


