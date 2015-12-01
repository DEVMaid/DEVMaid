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

package com.devmaid.web.controller.executors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.controller.executor.FsItemEx;
import com.devmaid.web.impl.StaticFsServiceFactory;
import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsService;

public class PasteCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor {
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception {
		String[] targets = request.getParameterValues("targets[]");
		boolean crossServerFromLocalToRemote = "-1".equals(request.getParameter("crossServerOperation"));
		boolean crossServerFromRemoteToLocal = "1".equals(request.getParameter("crossServerOperation"));
		String src = request.getParameter("src");
		String dst = request.getParameter("dst");
		boolean cut = "1".equals(request.getParameter("cut"));
		List<FsItemEx> added = new ArrayList<>();
		List<String> removed = new ArrayList<>();
		if (crossServerFromLocalToRemote) {
			// If it is a cross server pasting (i.e. copying from local to
			// remote or remote to local)
			StaticFsServiceFactory staticFsServiceFactory = StaticFsServiceFactory.getInstance();
			FsService localFsService = staticFsServiceFactory.getFsService();
			FsItem remoteFsItemDest = fsService.getFsItem(dst);
			String destRemotePath = remoteFsItemDest.getFile().getAbsolutePath();
			for (String target : targets) {
				FsItem localFsItemSrc = localFsService.getFsItem(target);
				String srcLocalPath = localFsItemSrc.getFile().getAbsolutePath();
				remoteFsItemDest.scpFrom(localFsItemSrc);
				System.out.println("target: " + target + ", srcLocalPath: " + srcLocalPath + ", destRemotePath: " + destRemotePath);
			}
			//Refresh the dest
			remoteFsItemDest.refresh();
		} else if (crossServerFromRemoteToLocal) {

		} else {
			FsItemEx fsrc = super.findItem(fsService, src);
			FsItemEx fdst = super.findItem(fsService, dst);

			for (String target : targets) {
				FsItemEx ftgt = super.findItem(fsService, target);
				String name = ftgt.getName();
				FsItemEx newFile = new FsItemEx(fdst, name);
				super.createAndCopy(ftgt, newFile);
				added.add(newFile);

				if (cut) {
					ftgt.delete();
					removed.add(target);
				}
			}
		}

		json.put("added", files2JsonArray(request, added));
		json.put("removed", removed);
	}
}
