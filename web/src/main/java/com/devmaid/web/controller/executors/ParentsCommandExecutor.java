package com.devmaid.web.controller.executors;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.controller.executor.FsItemEx;
import com.devmaid.web.service.FsService;

import com.devmaid.web.util.UtilWeb;

public class ParentsCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String target = request.getParameter("target");

		Map<String, FsItemEx> files = new HashMap<String, FsItemEx>();
		FsItemEx fsi = findItem(fsService, target);
		UtilWeb.instance().debug("In execute, started fsi: " + fsi);
		while (!fsi.isRoot())
		{
			super.addSubfolders(files, fsi);
			fsi = fsi.getParent();
			UtilWeb.instance().debug("In execute, this new fsi: " + fsi);
		}

		json.put("tree", files2JsonArray(request, files.values()));
	}
}
