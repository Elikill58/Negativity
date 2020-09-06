package com.elikill58.negativity.universal.ban;

public class BanResult {
	
	private final BanResultType resultType;
	private final Ban ban;
	
	public BanResult(BanResultType resultType, Ban ban) {
		this.resultType = resultType;
		this.ban = ban;
	}
	
	public Ban getBan() {
		return ban;
	}
	
	public BanResultType getResultType() {
		return resultType;
	}
	
	public boolean isSuccess() {
		return getResultType().isSuccess();
	}
	
	public static enum BanResultType {
		
		ALREADY_BANNED(false, "Already banned"),
		DONE(true, "Done"),
		EXCEPTION(false, "Internal error (check console)"),
		NOT_ENABLED(false, "Check your ban config"),
		UNKNOW_PLAYER(false, "Unknow Player"),
		UNKNOW_SERVICE(false, "Unknow Service"),
		UNKNOW_PROCESSOR(false, "Unknow Processor (check your config)");
		
		private final boolean success;
		private final String name;
		
		private BanResultType(boolean success, String name) {
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
