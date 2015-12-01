package com.devmaid.web.service;

import java.io.File;

public interface FsItem
{
	FsVolume getVolume();
	File getFile();
	boolean scpFrom(FsItem source);
	void refresh();
}
