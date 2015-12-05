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

package com.devmaid.web.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.devmaid.web.service.FsService;
import com.devmaid.web.service.FsServiceFactory;

/**
 * 
 * @author Ken Wu
 *
 */
public class StaticFsServiceFactory implements FsServiceFactory {
	static StaticFsServiceFactory _instance = null;
	protected FsService _localFsService;
	protected FsService _remoteFsService;

	@Override
	public FsService getFileService(HttpServletRequest request, ServletContext servletContext, boolean isRemote) {
		return getFsService(isRemote);
	}

	public FsService getFsService() {
		return getFsService(false);
	}
	
	public FsService getFsService(boolean remote) {
		if(remote) {
			return _remoteFsService;
		} else {
			return _localFsService;
		}
	}

	public void setFsService(FsService fsService, boolean remote) {
		if(remote) {
			_remoteFsService = fsService;
		} else {
			_localFsService = fsService;
		}
	}
	
	public void setFsService(FsService fsService) {
		setFsService(fsService, false);
	}

	public static StaticFsServiceFactory getInstance() {
		if (_instance == null) {
			synchronized (StaticFsServiceFactory.class) {
				if (_instance == null) {
					_instance = new StaticFsServiceFactory();
				}
			}
		}
		return _instance;
	}
}
