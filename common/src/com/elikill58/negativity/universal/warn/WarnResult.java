package com.elikill58.negativity.universal.warn;

public class WarnResult {
	
	private final WarnResultType resultType;
	private final Warn warn;
	
	public WarnResult(WarnResultType resultType) {
		this.resultType = resultType;
		this.warn = null;
	}
	
	public WarnResult(Warn warn) {
		this.resultType = WarnResultType.DONE;
		this.warn = warn;
	}
	
	public WarnResult(WarnResultType resultType, Warn warn) {
		this.resultType = resultType;
		this.warn = warn;
	}
	
	public Warn getWarn() {
		return warn;
	}
	
	public WarnResultType getResultType() {
		return resultType;
	}
	
	public boolean isSuccess() {
		return getResultType().isSuccess();
	}
	
	public enum WarnResultType {

		TOO_MANY(false, "Already unbanned"),
		BYPASS(false, "Bypass"),
		DONE(true, "Done"),
		EXCEPTION(false, "Internal error (check console)"),
		NOT_ENABLED(false, "Check your ban config"),
		UNKNOW_PLAYER(false, "Unknow Player"),
		UNKNOW_SERVICE(false, "Unknow Service"),
		UNKNOW_PROCESSOR(false, "Unknow Processor (check your config)");
		
		private final boolean success;
		private final String name;
		
		WarnResultType(boolean success, String name) {
			this.success = success;
			this.name = name;
		}
		
		public boolean isSuccess() {
			return success;
		}
		
		public String getName() {
			return name;
		}
	}
}
