package com.elikill58.negativity.sponge9;

import org.apache.logging.log4j.Logger;

import com.elikill58.negativity.universal.logger.LoggerAdapter;

public class Log4jAdapter implements LoggerAdapter {
    
    private final Logger logger;
    
    public Log4jAdapter(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void info(String msg) {
        this.logger.info(msg);
    }
    
    @Override
    public void warn(String msg) {
        this.logger.warn(msg);
    }
    
    @Override
    public void error(String msg) {
        this.logger.error(msg);
    }
}
