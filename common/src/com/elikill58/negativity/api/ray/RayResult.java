package com.elikill58.negativity.api.ray;

public enum RayResult {
	
	REACH_BOTTOM(true, false, true),
	REACH_TOP(true, false, true),
	NEEDED_FOUND(true, true, true),
	NEEDED_NOT_FOUND(true, false, true),
	TOO_FAR(true, false, false),
	FIND_OTHER(true, false, false),
	CONTINUE(false, false, false);
	
	private final boolean canFinish, founded, shouldFinish;
	
	RayResult(boolean canFinish, boolean founded, boolean shouldFinish) {
		this.canFinish = canFinish;
		this.founded = founded;
		this.shouldFinish = shouldFinish;
	}
	
	public boolean canFinish() {
		return canFinish;
	}
	
	public boolean isFounded() {
		return founded;
	}
	
	public boolean isShouldFinish() {
		return shouldFinish;
	}
}