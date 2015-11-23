package com.devmaid.web.controller.executors;

import com.devmaid.web.controller.ErrorException;
import com.devmaid.web.controller.executor.AbstractJsonCommandExecutor;
import com.devmaid.web.controller.executor.CommandExecutor;
import com.devmaid.web.service.FsService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * This is a command that should be executed when a matching command can't be found.
 */
public class MissingCommandExecutor extends AbstractJsonCommandExecutor implements CommandExecutor
{
    @Override
    protected void execute(FsService fsService, HttpServletRequest request, ServletContext servletContext, JSONObject json) throws Exception
    {
        String cmd = request.getParameter("cmd");
        throw new ErrorException("errUnknownCmd", cmd);
   }
}
