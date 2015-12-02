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

import java.io.ByteArrayInputStream;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.devmaid.web.remotefs.RemoteFile;
import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsItemFilter;
import com.devmaid.web.service.FsVolume;
import com.devmaid.web.service.KeywordFsItemFilter;
import com.devmaid.web.util.MimeTypesUtils;
import com.devmaid.web.util.UtilWeb;
import com.devmaid.common.Util;

public class RemoteFsVolume implements FsVolume {
	/**
	 * Used to calculate total file size when walking the tree.
	 */
	private static class FileSizeFileVisitor extends SimpleFileVisitor<Path> {

		private long totalSize;

		public long getTotalSize() {
			return totalSize;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			totalSize += file.toFile().length();
			return FileVisitResult.CONTINUE;
		}

	}

	String _name;

	RemoteFile _rootDir;

	private RemoteFile asFile(FsItem fsi) {
		return ((RemoteFsItem) fsi).getFile();
	}

	@Override
	public void createFile(FsItem fsi) throws IOException {
		asFile(fsi).createNewFile();
	}

	@Override
	public void createFolder(FsItem fsi) throws IOException {
		asFile(fsi).mkdirs();
	}

	@Override
	public void deleteFile(FsItem fsi) throws IOException {
		RemoteFile file = asFile(fsi);
		if (!file.isDirectory()) {
			file.delete();
		}
	}

	@Override
	public void deleteFolder(FsItem fsi) throws IOException {
		RemoteFile file = asFile(fsi);
		if (file.isDirectory()) {
			// FileUtils.deleteDirectory(file);
			file.deleteDir();
		}
	}

	@Override
	public boolean exists(FsItem newFile) {
		return asFile(newFile).exists();
	}

	private RemoteFsItem fromFile(RemoteFile file) {
		return new RemoteFsItem(this, file);
	}

	@Override
	public FsItem fromPath(String relativePath) {
		String absolutePath = Util.joinPath(_rootDir.getFileName(), relativePath);
		RemoteFile childrenFromRoot = findChilden(_rootDir, absolutePath);
		UtilWeb.debug("In RemoteFsVolume fromPath, given relativePath: " + relativePath + ", childrenFromRoot: "
				+ childrenFromRoot);
		if (childrenFromRoot != null) {
			return fromFile(childrenFromRoot);
		} else {
			RemoteFile rf = new RemoteFile(_rootDir, absolutePath);

			// Here i am looking for the parent dir
			String parentRelativePath = Util.getParentPath(relativePath);
			String parentAbsolutePath = Util.joinPath(_rootDir.getFileName(), parentRelativePath);
			RemoteFile parentFromRoot = findChilden(_rootDir, parentAbsolutePath);
			UtilWeb.debug("In RemoteFsVolume fromPath, given relativePath: " + relativePath + ", parentFromRoot: "
					+ parentFromRoot);
			rf.setConnectionIndex(_rootDir.getConnectionIndex());
			rf.setParentFile(parentFromRoot);
			return fromFile(rf);
		}
	}

	/*
	 * This method starts from the rootDir looking for the specified children
	 * given the relative path If nothing is found, return null
	 * 
	 * Prerequisites: startFrom cannot be null
	 */
	private RemoteFile findChilden(RemoteFile startFrom, String absolutePath) {
		if (startFrom.getCanonicalPath().equals(absolutePath))
			return startFrom;
		if (absolutePath.indexOf(startFrom.getFileName()) > -1) {
			RemoteFile[] rfs = startFrom.listFiles();
			for (int i = 0; i < rfs.length; i++) {
				if (rfs[i].getCanonicalPath().equals(absolutePath)) {
					return rfs[i];
				} else {
					// Calling itself recursively
					RemoteFile ans = findChilden(rfs[i], absolutePath);
					if (ans != null) {
						return ans;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getDimensions(FsItem fsi) {
		return null;
	}

	@Override
	public long getLastModified(FsItem fsi) {
		return asFile(fsi).lastModified();
	}

	@Override
	public String getMimeType(FsItem fsi) {
		RemoteFile file = asFile(fsi);
		if (file.isDirectory())
			return "directory";

		String ext = FilenameUtils.getExtension(file.getName());
		if (ext != null && !ext.isEmpty()) {
			String mimeType = MimeTypesUtils.getMimeType(ext);
			return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE : mimeType;
		}

		return MimeTypesUtils.UNKNOWN_MIME_TYPE;
	}

	public String getName() {
		return _name;
	}

	@Override
	public String getName(FsItem fsi) {
		return asFile(fsi).getName();
	}

	@Override
	public FsItem getParent(FsItem fsi) {
		return fromFile(asFile(fsi).getParentFile());
	}

	@Override
	public String getPath(FsItem fsi) throws IOException {
		String fullPath = asFile(fsi).getCanonicalPath();
		String rootPath = _rootDir.getCanonicalPath();
		String relativePath = fullPath.substring(rootPath.length());
		UtilWeb.instance().debug(
				"In RemoteFsVolume getPath, fullPath: " + fullPath + ", rootPath: " + rootPath + ", relativePath: "
						+ relativePath);
		return relativePath.replace('\\', '/');
	}

	@Override
	public FsItem getRoot() {
		return fromFile(_rootDir);
	}

	public RemoteFile getRootDir() {
		return _rootDir;
	}

	@Override
	public long getSize(FsItem fsi) throws IOException {
		if (isFolder(fsi)) {
			// This recursively walks down the tree
			// Path folder = asFile(fsi).toPath();
			// FileSizeFileVisitor visitor = new FileSizeFileVisitor();
			// Files.walkFileTree(folder, visitor);
			// return visitor.getTotalSize();

			// To be implemented

			return -1;
		} else {
			return asFile(fsi).length();
		}
	}

	@Override
	public String getThumbnailFileName(FsItem fsi) {
		return null;
	}

	@Override
	public String getURL(FsItem f) {
		// We are just happy to not supply a custom URL.
		return null;
	}

	@Override
	public boolean hasChildFolder(FsItem fsi) {
		return asFile(fsi).isDirectory() && asFile(fsi).listFiles(new FileFilter() {
			@Override
			public boolean accept(java.io.File arg0) {
				return arg0.isDirectory();
			}
		}).length > 0;
	}

	@Override
	public boolean isFolder(FsItem fsi) {
		return asFile(fsi).isDirectory();
	}

	@Override
	public boolean isRoot(FsItem fsi) {
		return _rootDir == asFile(fsi);
	}

	@Override
	public FsItem[] listChildren(FsItem fsi, KeywordFsItemFilter filter) {
		RemoteFile rf = asFile(fsi);
		List<FsItem> list = new ArrayList<FsItem>();
		// Here i am basically issuing the find command on current directory on
		// this rf RemoteFile
		RemoteFile[] cs = rf.find(filter.getKeyWord());
		for (RemoteFile c : cs) {
			list.add(fromFile(c));
		}
		return list.toArray(new FsItem[list.size()]);
	}

	@Override
	public FsItem[] listChildren(FsItem fsi) {
		List<FsItem> list = new ArrayList<FsItem>();
		RemoteFile[] cs = asFile(fsi).listFiles();
		if (cs == null) {
			return new FsItem[0];
		}

		for (RemoteFile c : cs) {
			list.add(fromFile(c));
		}

		return list.toArray(new FsItem[list.size()]);
	}

	@Override
	public InputStream openInputStream(FsItem fsi) throws IOException {
		if (fsi instanceof RemoteFsItem) {
			// This is a remote file
			RemoteFile rf = (RemoteFile) asFile(fsi);
			String tmpFileToWritenTo = Util.joinPath("/etc/DEVMaid/tmp/", rf.getRandomizedFileName());

			rf.scp(tmpFileToWritenTo);
			// } else {
			// String content = rf.cat();
			// Util.writeContentToLocalFile(tmpFileToWritenTo,content);
			// }
			return new RemoteFileInputStream(tmpFileToWritenTo);
		} else {
			// This is a local file
			return new FileInputStream(asFile(fsi));
		}

	}

	@Override
	public void rename(FsItem src, FsItem dst) throws IOException {
		asFile(src).renameTo(asFile(dst));
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRootDir(RemoteFile rootDir) {
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}

		_rootDir = rootDir;
	}

	@Override
	public String toString() {
		return "RemoteFsVolume [" + _name + ", " + _rootDir + "]";
	}

	@Override
	public void writeStream(FsItem fsi, InputStream is) throws IOException {
		String newContent = UtilWeb.readInputStreamIntoString(is);
		RemoteFile rf = (RemoteFile) asFile(fsi);
		rf.write(newContent);
		// To be implemented
		/*
		 * OutputStream os = null; try { os = new FileOutputStream(asFile(fsi));
		 * IOUtils.copy(is, os); } finally { if (is != null) { is.close(); } if
		 * (os != null) { os.close(); } }
		 */

	}

}
