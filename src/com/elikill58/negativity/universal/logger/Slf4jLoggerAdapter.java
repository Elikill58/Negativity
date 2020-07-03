package com.elikill58.negativity.universal.logger;

import org.slf4j.Logger;

public class Slf4jLoggerAdapter implements LoggerAdapter {

	private Logger logger;
	
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

}
