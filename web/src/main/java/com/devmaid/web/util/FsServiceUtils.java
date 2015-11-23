package com.devmaid.web.util;

import java.io.IOException;

import com.devmaid.web.controller.executor.FsItemEx;
import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsService;

public abstract class FsServiceUtils
{
	public static FsItemEx findItem(FsService fsService, String hash) throws IOException
	{
		FsItem fsi = fsService.fromHash(hash);
		if (fsi == null)
		{
			return null;
		}

		return new FsItemEx(fsi, fsService);
	}
}
