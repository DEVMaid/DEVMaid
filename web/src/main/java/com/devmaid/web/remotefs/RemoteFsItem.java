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

package com.devmaid.web.remotefs;


import java.util.ArrayList;

import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsVolume;

public class RemoteFsItem implements FsItem
{
	RemoteFile _file;

	FsVolume _volume;

	public RemoteFsItem(RemoteFsVolume volume, RemoteFile file)
	{
		super();
		_volume = volume;
		_file = file;
	}

	public RemoteFile getFile()
	{
		return _file;
	}

	public FsVolume getVolume()
	{
		return _volume;
	}

	public void setFile(RemoteFile file)
	{
		_file = file;
	}

	public void setVolume(FsVolume volume)
	{
		_volume = volume;
	}
	
	@Override
	public void refresh() {
		_file.refresh();
	}
	
	@Override
	public boolean scpFrom(FsItem source) {
		//For now, assuming source is a local source
		return _file.scpFrom(source.getFile().getAbsolutePath());
	}
	
	@Override
	public String toString() {
		return "[RemoteFile:" + this._file + ", volume:" + _volume +"]";
	}
}
