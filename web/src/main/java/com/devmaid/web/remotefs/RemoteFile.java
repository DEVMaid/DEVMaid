/*
Copyright (c) 2015 DEVMaid
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
# Author: DEVMaid
# Date: 2015
 * 
 */

package com.devmaid.web.remotefs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import scala.collection.immutable.List;

import com.devmaid.web.service.FsItem;
import com.devmaid.web.util.UtilWeb;
import com.devmaid.web.ssh.SshClient;
import com.devmaid.common.Util;
import java.util.UUID;

public class RemoteFile extends java.io.File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String _p;
	protected int _connectionIndex = 0;
	protected int _sourceIndex = 0;
	private RemoteFile _parent = null;
	private RemoteFile[] _allChildrens = null;
	private boolean _isDirectory = true;
	private long fileSize = -1;

	public RemoteFile(String relativePath) {
		super(relativePath);
		this._p = relativePath;
	}
	
	String sshCat() {
		return SshClient.cat(this._p, _connectionIndex, _sourceIndex, true);
	}
	
	public String cat() {
		return sshCat();
	}
	
	String sshLs() {
		return SshClient.ls(this._p, _connectionIndex, _sourceIndex, true);
	}
	
	private void construct() {
		String response = sshLs();

		/*
		 * Example of the output woule be like:
		 * 
		 * total 16 drwxrwxr-x 3 ubuntu ubuntu 4096 Nov 20 05:41 ./ drwxr-xr-x 8
		 * ubuntu ubuntu 4096 Nov 20 18:46 ../ drwxrwxr-x 6 ubuntu ubuntu 4096
		 * Nov 9 19:55 gis-tools-for-hadoop.wiki/ -rw-rw-r-- 1 ubuntu ubuntu 17
		 * Nov 20 05:41 README.txt
		 */
		
		UtilWeb.info("In RemoteFile construct, response: " + response);
		RemoteFileResponse[] allRemoteFileResponse = parse(this, response);
		this._allChildrens = constructRemoteFileFromRemoteFileResponse(this, allRemoteFileResponse);
	}

	public RemoteFile(RemoteFile o, String relativePath) {
		super(Util.joinPath(o._p, relativePath));
		this._p = o.getFile() + relativePath;
	}

	public int getConnectionIndex() {
		return this._connectionIndex;
	}

	public void setConnectionIndex(int connectionIndex) {
		this._connectionIndex = connectionIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this._sourceIndex = sourceIndex;
	}

	@Override
	public boolean createNewFile() {
		return true;
	}

	@Override
	public boolean delete() {
		return false;
	}

	public boolean deleteDir() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	String sshFind(String keyword) {
		return SshClient.find(this._p, keyword, _connectionIndex, _sourceIndex, true);
	}
	
	/*
	 * This function is to issue a find command remotely on the current
	 * directory and return a list of the RemoteFile which match the query
	 * 
	 * @keyword: it is used for the find command: find $this._p -iname
	 * '$keyword' 2>/dev/null
	 */
	public RemoteFile[] find(String keyword) {
		String response = sshFind(keyword);
		UtilWeb.info("In RemoteFile find, response: " + response);
		RemoteFileResponse[] allRemoteFileResponse = parse(this, response);
		RemoteFile[] allRemoteFiles= constructRemoteFileFromRemoteFileResponse(this, allRemoteFileResponse);
		return allRemoteFiles;
	}

	@Override
	public String getCanonicalPath() {
		return this._p;
	}

	public String getFile() {
		return this._p;
	}

	@Override
	public String getName() {
		return UtilWeb.instance().getName(_p);
	}

	public void setParentFile(RemoteFile pf) {
		this._parent = pf;
	}

	@Override
	public RemoteFile getParentFile() {
		return _parent;
	}

	@Override
	public long lastModified() {
		return -1;
	}

	@Override
	public RemoteFile[] listFiles(FileFilter filter) {
		if (_allChildrens == null) {
			construct();
		}
		ArrayList<RemoteFile> allfilteredChildrens = new ArrayList<RemoteFile>();
		for (int i = 0; i < _allChildrens.length; i++) {
			if (filter.accept(_allChildrens[i])) {
				allfilteredChildrens.add(_allChildrens[i]);
			}
		}
		return allfilteredChildrens.toArray(new RemoteFile[allfilteredChildrens.size()]);
	}

	@Override
	public RemoteFile[] listFiles() {
		if (_allChildrens == null) {
			construct();
		}
		return _allChildrens;
	}

	public long getFileSize() {
		return this.length();
	}
	
	public void setFileSize(long fileSizeInBytes) {
		this.fileSize = fileSizeInBytes;
	}
	
	@Override
	public long length() {

		return fileSize;
	}

	@Override
	public boolean mkdirs() {
		return true;
	}

	public void setDirectory(boolean isDir) {
		this._isDirectory = isDir;
	}

	@Override
	public boolean isDirectory() {
		return _isDirectory;
	}

	@Override
	public boolean renameTo(File o) {
		// To be implemented
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RemoteFile) {
			RemoteFile c = (RemoteFile) o;
			return c.getCanonicalPath().equals(this.getCanonicalPath());
		}
		return false;
	}

	@Override
	public String toString() {
		return this._p;
	}

	static class RemoteFileResponse {

		/*
		 * These are the index positions referring to the particular field from
		 * the ssh ls -lrt command response Example would be like: drwxrwxr-x 6
		 * ubuntu ubuntu 4096 Nov 9 19:55 gis-tools-for-hadoop.wiki
		 */
		public static final int INDEX_PERMISSIONINFOS = 0; // Something like
															// drwxr-xr-x
		public static final int INDEX_USER = 2;
		public static final int INDEX_GROUP = 3;
		public static final int INDEX_FILESIZE_IN_BYPTES = 4;
		public static final int INDEX_MODIFIED_MONTH = 5;
		public static final int INDEX_MODIFIED_DAY = 6;
		public static final int INDEX_MODIFIED_TIMEORYEAR = 7; // In mac or
																// linux*
		// machines, formatted will
		// be either 22:01 or 2015
		public static final int INDEX_PATH = 8;

		String permissionInfo;
		String numOfFileItemsInside;
		String user;
		String group;
		long fileSize;
		String month;
		int day;
		String timeOrYear;
		String path; // It will be whatever returned by the ssh command
	}

	/*
	 * The response will be an array of formats like this: -rwxr-xr-x 1 ken
	 * staff 211 Dec 13 2014 /Users/ken/workspace/luigi/scripts/ci/run_tests.sh*
	 */
	private static RemoteFileResponse[] parse(RemoteFile currentFile, String response) {
		String[] responseRows = response.split("\n");
		ArrayList<RemoteFileResponse> allRemoteFileResponses = new ArrayList<RemoteFileResponse>();
		for (int i = 0; i < responseRows.length; i++) {
			String[] responseAry = responseRows[i].split("\\s+");
			if (responseAry.length < 3) {
				// Most likely this row is not a valid row, so skip it
				continue;
			} else {
				String path = responseAry[RemoteFileResponse.INDEX_PATH];
				if (path.equals("./")) {
					// current direcotry
					currentFile._isDirectory = true;
				} else if (path.equals("../")) {
					// parent direcotry, does nothing for now
				} else {
					RemoteFileResponse rfResponse = new RemoteFileResponse();
					rfResponse.permissionInfo = responseAry[RemoteFileResponse.INDEX_PERMISSIONINFOS];
					rfResponse.user = responseAry[RemoteFileResponse.INDEX_USER];
					rfResponse.group = responseAry[RemoteFileResponse.INDEX_GROUP];
					rfResponse.fileSize = Long.parseLong(responseAry[RemoteFileResponse.INDEX_FILESIZE_IN_BYPTES]);
					rfResponse.month = responseAry[RemoteFileResponse.INDEX_MODIFIED_MONTH];
					rfResponse.day = Integer.parseInt(responseAry[RemoteFileResponse.INDEX_MODIFIED_DAY]);
					rfResponse.timeOrYear = responseAry[RemoteFileResponse.INDEX_MODIFIED_TIMEORYEAR];
					rfResponse.path = path;
					allRemoteFileResponses.add(rfResponse);
				}
			}
		}// end the for loop
		return allRemoteFileResponses.toArray(new RemoteFileResponse[allRemoteFileResponses.size()]);
	}
	
	private static RemoteFile[] constructRemoteFileFromRemoteFileResponse(RemoteFile currentFile, RemoteFileResponse[] allRemoteFileResponses) {
		ArrayList<RemoteFile> allRemoteFiles = new ArrayList<RemoteFile>();
		for (int i = 0; i < allRemoteFileResponses.length; i++) {;
			String wholeRelativePath = Util.joinPath(currentFile._p, allRemoteFileResponses[i].path);
			if (allRemoteFileResponses[i].path.charAt(0) == '/') {	//If it is absolute path already, then just take that absolute path without joining
				wholeRelativePath = allRemoteFileResponses[i].path;
			}
			RemoteFile subFile = new RemoteFile(wholeRelativePath);
			subFile.setConnectionIndex(currentFile._connectionIndex);
			subFile.setParentFile(currentFile);
			//UtilWeb.debug("In RemoteFile construct, wholeRelativePath: " + wholeRelativePath);
			subFile.setSourceIndex(currentFile._sourceIndex);
			if (allRemoteFileResponses[i].permissionInfo.charAt(0) == 'd') {
				subFile.setDirectory(true);
			} else {
				subFile.setDirectory(false);
			}
			subFile.setFileSize(allRemoteFileResponses[i].fileSize);
			allRemoteFiles.add(subFile);

		}// end the for loop
		return allRemoteFiles.toArray(new RemoteFile[allRemoteFiles.size()]);
	}

	//This method is to write a string of new content to this file through ssh
	public void write(String newContent) {
		sshWrite(newContent);
	}
	
	String sshWrite(String newContent) {
		return SshClient.write(this._p, newContent, _connectionIndex, _sourceIndex, true);
	}
	
	
	public String getRandomizedFileName() {
		return Util.getFileName(this._p) + "_" + UUID.randomUUID();
	}

}