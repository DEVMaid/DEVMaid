package com.devmaid.web.impl;

import java.io.IOException;
import java.util.List;

import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsSecurityChecker;
import com.devmaid.web.service.FsService;

public class FsSecurityCheckerChain implements FsSecurityChecker
{
	private static final FsSecurityChecker DEFAULT_SECURITY_CHECKER = new FsSecurityCheckForAll();

	List<FsSecurityCheckFilterMapping> _filterMappings;

	private FsSecurityChecker getChecker(FsService fsService, FsItem fsi) throws IOException
	{
		String hash = fsService.getHash(fsi);
		for (FsSecurityCheckFilterMapping mapping : _filterMappings)
		{
			if (mapping.matches(hash))
			{
				return mapping.getChecker();
			}
		}

		return DEFAULT_SECURITY_CHECKER;
	}

	public List<FsSecurityCheckFilterMapping> getFilterMappings()
	{
		return _filterMappings;
	}

	@Override
	public boolean isLocked(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isLocked(fsService, fsi);
	}

	@Override
	public boolean isReadable(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isReadable(fsService, fsi);
	}

	@Override
	public boolean isWritable(FsService fsService, FsItem fsi) throws IOException
	{
		return getChecker(fsService, fsi).isWritable(fsService, fsi);
	}

	public void setFilterMappings(List<FsSecurityCheckFilterMapping> filterMappings)
	{
		_filterMappings = filterMappings;
	}
}
