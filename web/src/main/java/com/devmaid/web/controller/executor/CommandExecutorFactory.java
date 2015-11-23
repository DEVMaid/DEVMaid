package com.devmaid.web.controller.executor;

public interface CommandExecutorFactory
{
	CommandExecutor get(String commandName);
}
