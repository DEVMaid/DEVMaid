package com.devmaid.web.controller.executor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.devmaid.web.controller.ErrorException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.devmaid.web.service.FsService;
import com.devmaid.web.util.UtilWeb;

public abstract class AbstractJsonCommandExecutor extends AbstractCommandExecutor
{
	@Override
	final public void execute(FsService fsService, HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception
	{
		JSONObject json = new JSONObject();
		try
		{
			execute(fsService, request, servletContext, json);
		}
		catch (ErrorException e)
		{
			if (e.getArgs() == null || e.getArgs().length == 0)
			{
				json.put("error", e.getError());
			}
			else
			{
				JSONArray errors = new JSONArray();
				errors.put(e.getError());
				for (String arg: e.getArgs())
				{
					errors.put(arg);
				}
				json.put("error", errors);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			json.put("error", e.getMessage());
		}
		finally
		{
			flushJSONResponse(response, json, true);
		}
	}

	public static void flushJSONResponse(HttpServletResponse response, JSONObject json, boolean outputAsTextHtml) throws IOException, JSONException {
		if(outputAsTextHtml) {
			response.setContentType("text/html; charset=UTF-8");
		} else {
			response.setContentType("application/json; charset=UTF-8");
		}
		PrintWriter writer = response.getWriter();
		json.write(writer);
		UtilWeb.instance().debug("In flushJSONResponse, json: " + json);
		writer.flush();
		writer.close();
	}
	
	protected abstract void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext,
			JSONObject json) throws Exception;

}
