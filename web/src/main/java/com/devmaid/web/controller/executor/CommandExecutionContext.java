package com.devmaid.web.controller.executor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devmaid.web.service.FsServiceFactory;
import com.devmaid.web.data.TerminalRequest;

public interface CommandExecutionContext
{
	FsServiceFactory getFsServiceFactory();

	HttpServletRequest getRequest();

	HttpServletResponse getResponse();

	ServletContext getServletContext();

	boolean isRemote();
	
	TerminalRequest getTerminalRequest();
}
