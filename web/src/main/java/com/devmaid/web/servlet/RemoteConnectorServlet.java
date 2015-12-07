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

package com.devmaid.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devmaid.common.Util;
import com.devmaid.common.config.Connection;
import com.devmaid.web.controller.ConnectorController;
import com.devmaid.web.controller.executor.CommandExecutorFactory;
import com.devmaid.web.controller.executor.DefaultCommandExecutorFactory;
import com.devmaid.web.controller.executors.MissingCommandExecutor;
import com.devmaid.web.impl.DefaultFsService;
import com.devmaid.web.impl.DefaultFsServiceConfig;
import com.devmaid.web.impl.FsSecurityCheckForAll;
import com.devmaid.web.impl.StaticFsServiceFactory;
import com.devmaid.web.remotefs.RemoteFsVolume;
import com.devmaid.web.remotefs.RemoteFile;
import com.devmaid.web.util.Log;
import com.devmaid.web.util.UtilWeb;
import com.devmaid.web.JettyLauncher;
import com.devmaid.web.service.FileRepository;
import com.devmaid.web.ssh.SshClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class works like ConnectorServlet but it is for remote connection logic instead of local
 */

public class RemoteConnectorServlet extends ConnectorServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean _isRemote = true;		//Override it to true
	
	@Override
	protected boolean getRemote() {
		// TODO Auto-generated method stub
		return _isRemote;
	}

	@Override
	protected StaticFsServiceFactory createServiceFactory()
	{
		StaticFsServiceFactory staticFsServiceFactory = StaticFsServiceFactory.getInstance();
		DefaultFsService fsService = createFsService();
		staticFsServiceFactory.setFsService(fsService, true);
		return staticFsServiceFactory;
	}
	
	private RemoteFsVolume ceateRemoteFsVolume(int sourceIndex, String name, RemoteFile rootDir)
	{
		RemoteFsVolume remoteFsVolume = new RemoteFsVolume();
		rootDir.setSourceIndex(sourceIndex);
		remoteFsVolume.setName(name);
		remoteFsVolume.setRootDir(rootDir);
		return remoteFsVolume;
	}

	protected DefaultFsService createFsService()
	{
		DefaultFsService fsService = new DefaultFsService();
		fsService.setSecurityChecker(new FsSecurityCheckForAll());

		DefaultFsServiceConfig serviceConfig = new DefaultFsServiceConfig();
		serviceConfig.setTmbWidth(80);

		fsService.setServiceConfig(serviceConfig);

		List<String> destRoots = JettyLauncher.instance().destinationRootsAsJava();
		logger.info("In createFsService, size of destRoots: " + destRoots.size());
		List<Connection> conns = JettyLauncher.instance().connectionsAsJava();
		for(int connectionIndex=0; connectionIndex<conns.size(); connectionIndex++) {
			Connection conn = conns.get(connectionIndex);
			for(int i=0; i<destRoots.size(); i++) {
				String destRoot = destRoots.get(i);
				//String dirName = UtilWeb.instance().getName(destRoot);
				String repName = conn.hostname() + ":"+destRoot+"";
				RemoteFile rootRf = FileRepository.getInstance().getRemoteFile(connectionIndex, destRoot);
				rootRf.setConnectionIndex(connectionIndex);
				fsService.addVolume("RemoteSource"+i,
						ceateRemoteFsVolume(i, repName, rootRf));
			}	
		}
		
		//fsService.addVolume("A",
		//		ceateLocalFsVolume("My Files", new File("/Users/ken/workspace/analysis/")));
		//fsService.addVolume("B",
		//		ceateLocalFsVolume("Shared", new File("/tmp/")));
		
		return fsService;
	}

}
