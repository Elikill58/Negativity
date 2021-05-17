package com.elikill58.negativity.universal.ban;

import java.util.Objects;

public class BanResult {
	
	private final BanResultType resultType;
	private final Ban ban;
	
	public BanResult(BanResultType resultType) {
		this.resultType = resultType;
		this.ban = null;
	}
	
	public BanResult(Ban ban) {
		this.resultType = BanResultType.DONE;
		this.ban = ban;
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BanResult banResult = (BanResult) o;
		return resultType == banResult.resultType && Objects.equals(ban, banResult.ban);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(resultType, ban);
	}
	
	@Override
	public String toString() {
		return "BanResult{resultType=" + resultType + ", ban=" + ban + '}';
	}
	
	public enum BanResultType {

		ALREADY_BANNED(false, "Already banned"),
		ALREADY_UNBANNED(false, "Already unbanned"),
		BYPASS(false, "Bypass"),
		DONE(true, "Done"),
		EXCEPTION(false, "Internal error (check console)"),
		NOT_ENABLED(false, "Check your ban config"),
		UNKNOW_PLAYER(false, "Unknow Player"),
		UNKNOW_SERVICE(false, "Unknow Service"),
		UNKNOW_PROCESSOR(false, "Unknow Processor (check your config)");
		
		private final boolean success;
		private final String name;
		
		BanResultType(boolean success, String name) {
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
