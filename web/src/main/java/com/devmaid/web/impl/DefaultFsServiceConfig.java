package com.devmaid.web.impl;

import com.devmaid.web.service.FsServiceConfig;

public class DefaultFsServiceConfig implements FsServiceConfig
{
	private int _tmbWidth;

	public void setTmbWidth(int tmbWidth)
	{
		_tmbWidth = tmbWidth;
	}

	@Override
	public int getTmbWidth()
	{
		return _tmbWidth;
	}
}
