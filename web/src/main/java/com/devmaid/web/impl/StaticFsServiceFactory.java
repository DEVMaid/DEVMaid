package com.devmaid.web.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.devmaid.web.service.FsService;
import com.devmaid.web.service.FsServiceFactory;

/**
 * A StaticFsServiceFactory always returns one FsService, despite of whatever it is requested 
 * 
 * @author bluejoe
 *
 */
public class StaticFsServiceFactory implements FsServiceFactory
{
	FsService _fsService;

	@Override
	public FsService getFileService(HttpServletRequest request, ServletContext servletContext)
	{
		return _fsService;
	}

	public FsService getFsService()
	{
		return _fsService;
	}

	public void setFsService(FsService fsService)
	{
		_fsService = fsService;
	}
}
