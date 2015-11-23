package com.devmaid.web.controller.executors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.controller.executor.FsItemEx;
import com.devmaid.web.service.FsService;

public class RmCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
	@Override
	public void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json)
			throws Exception
	{
		String[] targets = request.getParameterValues("targets[]");
		String current = request.getParameter("current");
		List<String> removed = new ArrayList<String>();

		for (String target : targets)
		{
			FsItemEx ftgt = super.findItem(fsService, target);
			ftgt.delete();
			removed.add(ftgt.getHash());
		}

		json.put("removed", removed.toArray());
	}
}
