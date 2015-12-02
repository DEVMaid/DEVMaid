package com.devmaid.web.localfs;

import java.io.File;

import com.devmaid.web.remotefs.RemoteFile;
import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsVolume;

public class LocalFsItem implements FsItem {
	File _file;

	FsVolume _volume;

	public LocalFsItem(LocalFsVolume volume, java.io.File file) {
		super();
		_volume = volume;
		_file = file;
	}

	@Override
	public File getFile() {
		return _file;
	}

	public FsVolume getVolume() {
		return _volume;
	}

	public void setFile(File file) {
		_file = file;
	}

	public void setVolume(FsVolume volume) {
		_volume = volume;
	}

	@Override
	public void refresh() {
		//To be implemented --- actually not quite needed
	}
	
	@Override
	public boolean scpFrom(FsItem source) {
		// For now, assuming source is a remote source
		RemoteFile rf = (RemoteFile) source.getFile();
		return rf.scpFrom(this._file.getAbsolutePath());
	}

	@Override
	public String toString() {
		return "[LocalFile:" + this._file + ", volume:" + _volume + "]";
	}
}
