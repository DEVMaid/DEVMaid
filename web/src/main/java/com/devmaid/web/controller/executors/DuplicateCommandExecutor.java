package com.devmaid.web.controller.executors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.controller.executor.FsItemEx;
import com.devmaid.web.service.FsService;

public class DuplicateCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String[] targets = request.getParameterValues("targets[]");

		List<FsItemEx> added = new ArrayList<FsItemEx>();

		for (String target : targets)
		{
			FsItemEx fsi = super.findItem(fsService, target);
			String name = fsi.getName();
			String baseName = FilenameUtils.getBaseName(name);
			String extension = FilenameUtils.getExtension(name);

			int i = 1;
			FsItemEx newFile = null;
			baseName = baseName.replaceAll("\\(\\d+\\)$", "");

			while (true)
			{
				String newName = String.format("%s(%d)%s", baseName, i, (extension == null || extension.isEmpty() ? ""
						: "." + extension));
				newFile = new FsItemEx(fsi.getParent(), newName);
				if (!newFile.exists())
				{
					break;
				}
				i++;
			}

			createAndCopy(fsi, newFile);
			added.add(newFile);
		}

		json.put("added", files2JsonArray(request, added));
	}

}
