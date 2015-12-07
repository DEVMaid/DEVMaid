/*
Copyright (c) 2015 DEVMaid
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
# Author: DEVMaid
# Date: 2015
 * 
 */

package com.devmaid.web.service;

import com.devmaid.web.remotefs.RemoteFile;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileRepository {
	private static FileRepository _fr = new FileRepository();
	private final Map<FileRepositoryKey,File> _fileRepoMap =
		    Collections.synchronizedMap(new HashMap<FileRepositoryKey,File>(1000));

	// make the constructor private so that this class cannot be
	// instantiated
	private FileRepository() {
	}

	public File getLocalFile(int connectionIndex, String absolutePath) {
		return (File)getFile(connectionIndex+"", absolutePath, false);
	}
	
	public RemoteFile getRemoteFile(int connectionIndex, String absolutePath) {
		return (RemoteFile)getFile(connectionIndex+"", absolutePath, true);
	}
	
	private File getFile(String connectionIndex, String absolutePath, boolean isRemote) {
		FileRepositoryKey key = new FileRepositoryKey(connectionIndex, absolutePath);
		if(_fileRepoMap.containsKey(key)){
			return _fileRepoMap.get(key);
		} else {
			File f;
			if(isRemote) {
				f = new RemoteFile(absolutePath);
			} else {
				f = new File(absolutePath);
			}
			_fileRepoMap.put(key, f);
			return f;
		}
	}
	
	public static FileRepository getInstance() {
		return _fr;
	}
	
	private static class FileRepositoryKey {
		String connectionIndex;
		String absolutePath;
		
		FileRepositoryKey(String hn, String ap) {
			this.connectionIndex=hn;
			this.absolutePath=ap;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
			if (other == this) return true;
		    if (!(other instanceof FileRepositoryKey))return false;
		    FileRepositoryKey otherMyClass = (FileRepositoryKey)other;
		    return otherMyClass.connectionIndex.equalsIgnoreCase(this.connectionIndex) && (new File(otherMyClass.absolutePath).equals(new File(this.absolutePath)));
		}
		
		@Override
	    public int hashCode() {
			final int prime = 31;
            int result = 1;
            result = prime * result
                         + ((connectionIndex == null) ? 0 : connectionIndex.hashCode());
            result = prime * result
                         + ((absolutePath == null) ? 0 : absolutePath.hashCode());
            return result;
        }
	}
}