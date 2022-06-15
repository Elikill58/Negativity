package com.elikill58.negativity.universal.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLoggerAdapter implements LoggerAdapter {

	private final Logger logger;
	
	public JavaLoggerAdapter(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		logger.warning(msg);
	}

	@Override
	public void error(String msg) {
		logger.severe(msg);
	}

	@Override
	public void printError(String message, Exception e) {
		logger.log(Level.SEVERE, message, e);
	}
}
