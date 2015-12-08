package com.devmaid.web.localfs;

import java.io.File;
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

import com.devmaid.common.Util;
import com.devmaid.web.service.FileRepository;
import com.devmaid.web.service.FsItem;
import com.devmaid.web.service.FsItemFilter;
import com.devmaid.web.service.FsVolume;
import com.devmaid.web.service.KeywordFsItemFilter;
import com.devmaid.web.util.MimeTypesUtils;

public class LocalFsVolume implements FsVolume
{
	/**
	 * Used to calculate total file size when walking the tree.
	 */
	private static class FileSizeFileVisitor extends SimpleFileVisitor<Path>
	{

		private long totalSize;

		public long getTotalSize()
		{
			return totalSize;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException
		{
			totalSize += file.toFile().length();
			return FileVisitResult.CONTINUE;
		}

	}

	String _name;

	File _rootDir;

	private File asFile(FsItem fsi)
	{
		return ((LocalFsItem) fsi).getFile();
	}

	@Override
	public void createFile(FsItem fsi) throws IOException
	{
		asFile(fsi).createNewFile();
	}

	@Override
	public void createFolder(FsItem fsi) throws IOException
	{
		asFile(fsi).mkdirs();
	}

	@Override
	public void deleteFile(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (!file.isDirectory())
		{
			file.delete();
		}
	}

	@Override
	public void deleteFolder(FsItem fsi) throws IOException
	{
		File file = asFile(fsi);
		if (file.isDirectory())
		{
			FileUtils.deleteDirectory(file);
		}
	}

	@Override
	public boolean exists(FsItem newFile)
	{
		return asFile(newFile).exists();
	}

	private LocalFsItem fromFile(java.io.File file)
	{
		return new LocalFsItem(this, file);
	}

	@Override
	public FsItem fromPath(String relativePath)
	{
		File f = FileRepository.getInstance().getLocalFile(0, Util.joinPath(_rootDir.getAbsolutePath(), relativePath));
		return fromFile(f);
	}

	@Override
	public String getDimensions(FsItem fsi)
	{
		return null;
	}

	@Override
	public long getLastModified(FsItem fsi)
	{
		return asFile(fsi).lastModified();
	}

	@Override
	public String getMimeType(FsItem fsi)
	{
		File file = asFile(fsi);
		if (file.isDirectory())
			return "directory";

		String ext = FilenameUtils.getExtension(file.getName());
		if (ext != null && !ext.isEmpty())
		{
			String mimeType = MimeTypesUtils.getMimeType(ext);
			return mimeType == null ? MimeTypesUtils.UNKNOWN_MIME_TYPE
					: mimeType;
		}

		return MimeTypesUtils.UNKNOWN_MIME_TYPE;
	}

	public String getName()
	{
		return _name;
	}

	@Override
	public String getName(FsItem fsi)
	{
		return asFile(fsi).getName();
	}

	@Override
	public FsItem getParent(FsItem fsi)
	{
		return fromFile(asFile(fsi).getParentFile());
	}

	@Override
	public String getPath(FsItem fsi) throws IOException
	{
		String fullPath = asFile(fsi).getCanonicalPath();
		String rootPath = _rootDir.getCanonicalPath();
		try{
			String relativePath = fullPath.substring(rootPath.length());
			return relativePath.replace('\\', '/');
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public FsItem getRoot()
	{
		return fromFile(_rootDir);
	}

	public File getRootDir()
	{
		return _rootDir;
	}

	@Override
	public long getSize(FsItem fsi) throws IOException
	{
		if (isFolder(fsi))
		{
			// This recursively walks down the tree
			Path folder = asFile(fsi).toPath();
			FileSizeFileVisitor visitor = new FileSizeFileVisitor();
			Files.walkFileTree(folder, visitor);
			return visitor.getTotalSize();
		}
		else
		{
			return asFile(fsi).length();
		}
	}

	@Override
	public String getThumbnailFileName(FsItem fsi)
	{
		return null;
	}

	@Override
	public String getURL(FsItem f)
	{
		// We are just happy to not supply a custom URL.
		return null;
	}

	@Override
	public boolean hasChildFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory()
				&& asFile(fsi).listFiles(new FileFilter()
				{

					@Override
					public boolean accept(java.io.File arg0)
					{
						return arg0.isDirectory();
					}
				}).length > 0;
	}

	@Override
	public boolean isFolder(FsItem fsi)
	{
		return asFile(fsi).isDirectory();
	}

	@Override
	public boolean isRoot(FsItem fsi)
	{
		return _rootDir == asFile(fsi);
	}

	@Override
	public FsItem[] listChildren(FsItem fsi, KeywordFsItemFilter filter){
		List<FsItem> list = new ArrayList<FsItem>();
		java.io.File[] cs = (asFile(fsi).listFiles());
		if (cs == null)
		{
			return new FsItem[0];
		}

		for (java.io.File c : cs)
		{
			
				list.add(fromFile(c));
		}

		return list.toArray(new FsItem[0]);
	}
	
	@Override
	public FsItem[] listChildren(FsItem fsi)
	{
		return listChildren(fsi, null);
	}

	@Override
	public void rename(FsItem src, FsItem dst) throws IOException
	{
		asFile(src).renameTo(asFile(dst));
	}

	public void setName(String name)
	{
		_name = name;
	}

	public void setRootDir(File rootDir)
	{
		if (!rootDir.exists())
		{
			rootDir.mkdirs();
		}

		_rootDir = rootDir;
	}

	@Override
	public String toString()
	{
		return "LocalFsVolume [" + _rootDir + "]";
	}

	@Override
	public void writeStream(FsItem fsi, InputStream is) throws IOException
	{
		OutputStream os = null;
		try
		{
			os = new FileOutputStream(asFile(fsi));
			IOUtils.copy(is, os);
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
			if (os != null)
			{
				os.close();
			}
		}
	}

	@Override
	public InputStream openInputStream(FsItem fsi) throws IOException {
		return new FileInputStream(asFile(fsi));
	}
}
