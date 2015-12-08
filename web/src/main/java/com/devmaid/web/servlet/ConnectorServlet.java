package com.devmaid.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devmaid.common.Util;
import com.devmaid.web.controller.ConnectorController;
import com.devmaid.web.controller.executor.CommandExecutorFactory;
import com.devmaid.web.controller.executor.DefaultCommandExecutorFactory;
import com.devmaid.web.controller.executors.MissingCommandExecutor;
import com.devmaid.web.impl.DefaultFsService;
import com.devmaid.web.impl.DefaultFsServiceConfig;
import com.devmaid.web.impl.FsSecurityCheckForAll;
import com.devmaid.web.impl.StaticFsServiceFactory;
import com.devmaid.web.localfs.LocalFsVolume;
import com.devmaid.web.service.FileRepository;
import com.devmaid.web.util.Log;
import com.devmaid.web.util.UtilWeb;
import com.devmaid.web.JettyLauncher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//core member of this Servlet
	ConnectorController _connectorController;
	Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
	private boolean _isThisRemote = false;
	
	protected boolean getRemote() {
		// TODO Auto-generated method stub
		return _isThisRemote;
	}

	private LocalFsVolume ceateLocalFsVolume(String name, File rootDir)
	{
		LocalFsVolume localFsVolume = new LocalFsVolume();
		localFsVolume.setName(name);
		localFsVolume.setRootDir(rootDir);
		return localFsVolume;
	}

	/**
	 * create a command executor factory
	 * 
	 * @param config
	 * @return
	 */
	protected CommandExecutorFactory createCommandExecutorFactory()
	{
		DefaultCommandExecutorFactory defaultCommandExecutorFactory = new DefaultCommandExecutorFactory();
		defaultCommandExecutorFactory
				.setClassNamePattern("com.devmaid.web.controller.executors.%sCommandExecutor");
		defaultCommandExecutorFactory
				.setFallbackCommand(new MissingCommandExecutor());
		return defaultCommandExecutorFactory;
	}

	/**
	 * create a connector controller
	 * 
	 * @param config
	 * @return
	 */
	protected ConnectorController createConnectorController(ServletConfig config)
	{
		ConnectorController connectorController = new ConnectorController();

		connectorController
				.setCommandExecutorFactory(createCommandExecutorFactory());
		connectorController.setFsServiceFactory(createServiceFactory());

		return connectorController;
	}

	protected DefaultFsService createFsService()
	{
		DefaultFsService fsService = new DefaultFsService();
		fsService.setSecurityChecker(new FsSecurityCheckForAll());

		DefaultFsServiceConfig serviceConfig = new DefaultFsServiceConfig();
		serviceConfig.setTmbWidth(80);

		fsService.setServiceConfig(serviceConfig);

		List<String> sourceRoots = JettyLauncher.instance().sourceRootsAsJava();
		logger.info("In createFsService, size of sourceRoots: " + sourceRoots.size());
		for(int i=0; i<sourceRoots.size(); i++) {
			String sourceRoot = sourceRoots.get(i);
			String dirName = UtilWeb.instance().getName(sourceRoot);
			String repName = dirName + " ("+sourceRoot+")";
			File f = FileRepository.getInstance().getLocalFile(0, sourceRoot);
			fsService.addVolume("LocalSource"+i,
					ceateLocalFsVolume(repName, f));
		}
		//fsService.addVolume("A",
		//		ceateLocalFsVolume("My Files", new File("/Users/ken/workspace/analysis/")));
		//fsService.addVolume("B",
		//		ceateLocalFsVolume("Shared", new File("/tmp/")));
		return fsService;
	}

	/**
	 * create a service factory
	 * 
	 * @param config
	 * @return
	 */
	protected StaticFsServiceFactory createServiceFactory()
	{
		StaticFsServiceFactory staticFsServiceFactory = StaticFsServiceFactory.getInstance();
		DefaultFsService fsService = createFsService();

		staticFsServiceFactory.setFsService(fsService);
		return staticFsServiceFactory;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp, getRemote() );
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp, getRemote());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		_connectorController.connector(req, resp, getRemote());
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		_connectorController = createConnectorController(config);
	}
	
}
