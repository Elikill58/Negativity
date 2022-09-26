package com.elikill58.negativity.fabric;

import org.slf4j.Logger;

import com.elikill58.negativity.universal.logger.LoggerAdapter;

public class Slf4jLoggerAdapter implements LoggerAdapter {

	private Logger logger;
	
	public Slf4jLoggerAdapter(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void info(String msg) {
		logger.info("[Negativity] " +  msg);
	}

	@Override
	public void warn(String msg) {
		logger.warn("[Negativity] " +  msg);
	}

	@Override
	public void error(String msg) {
		logger.error("[Negativity] " +  msg);
	}

	@Override
	public void printError(String message, Exception e) {
		logger.error(message, e);
	}
}
