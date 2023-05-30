package com.elikill58.negativity.velocity;

import org.slf4j.Logger;

import com.elikill58.negativity.universal.logger.LoggerAdapter;

public class Slf4jLoggerAdapter implements LoggerAdapter {
	
	private final Logger logger;
	
	public Slf4jLoggerAdapter(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void info(String msg) {
		logger.info(msg);
	}
	
	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}
	
	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void printError(String message, Throwable e) {
		logger.error(message, e);
	}
}
