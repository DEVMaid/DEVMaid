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

public class LsCommandExecutor extends AbstractJsonCommandExecutor implements
		CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			ServletContext servletContext, JSONObject json) throws Exception
	{
		String target = request.getParameter("target");
		String[] onlyMimes = request.getParameterValues("mimes[]");

		Map<String, FsItemEx> files = new HashMap<String, FsItemEx>();
		FsItemEx fsi = super.findItem(fsService, target);
		super.addChildren(files, fsi, onlyMimes);

		json.put("list", files2JsonArray(request, files.values()));
	}
}
