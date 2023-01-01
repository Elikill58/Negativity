package com.elikill58.negativity.universal.logger;

public interface LoggerAdapter {

	public void info(String msg);
	
	public void warn(String msg);
	
	public void error(String msg);
	
	void printError(String message, Exception e);
}
