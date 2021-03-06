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
# Author: Ken Wu
# Date: 2015
 * 
 * - This class serves all commands issued by the clients and send it back the results in Json format 
 */

package com.devmaid.web.controller.executors;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutionContext;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.service.FsService;
import com.devmaid.web.data.TerminalRequest;
import com.devmaid.web.data.TerminalResponse;
import com.devmaid.web.ssh.SshClient;
import com.devmaid.web.util.UtilWeb;

public class TerminalCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor {
	@Override
	public void execute(CommandExecutionContext ctx) throws Exception {
		TerminalRequest tRequest = ctx.getTerminalRequest();
		JSONObject json = new JSONObject();
		TerminalResponse tResponse = SshClient.exec(tRequest.currentDir(), tRequest.command(), tRequest.connectionIndex());
		UtilWeb.info("In TerminalCommandExecutor execute, tResponse: " + tResponse + ", directoryItems: " + tResponse.getDirectoryItems());
		if(tResponse.sucess()) {
			json.put("result", tResponse.getOutput());
			json.put("resultWorkingDir", tResponse.getResultWorkingDir());
			json.put("filesDirsName", tResponse.getDirectoryItems());
		} else {
			json.put("error", "true");
			json.put("errorMsg", tResponse.getOutput());
		}
		
		flushJSONResponse(ctx.getResponse(), json, false);
	}
	
	@Override
	protected void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext,
			JSONObject json) throws Exception {
		// This method does nothing
	}
}
