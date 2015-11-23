package com.devmaid.web.service;

import com.devmaid.web.controller.executor.FsItemEx;


/**
 * A FsItemFilter tells if a FsItem is matched or not
 * 
 * @author bluejoe
 *
 */
public interface FsItemFilter
{
	public boolean accepts(FsItemEx item);
}
