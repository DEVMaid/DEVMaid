package com.devmaid.web.controller.executor;

public interface CommandExecutor
{
	void execute(CommandExecutionContext commandExecutionContext) throws Exception;
}
