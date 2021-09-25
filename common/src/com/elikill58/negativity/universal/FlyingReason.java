package com.elikill58.negativity.universal;

public enum FlyingReason {
	POTION(CheatKeys.ANTI_POTION),
	REGEN(CheatKeys.REGEN),
	EAT(CheatKeys.FAST_EAT),
	BOW(CheatKeys.FAST_BOW);
	
	private final CheatKeys key;
	
	FlyingReason(CheatKeys key) {
		this.key = key;
	}
	
	public CheatKeys getKey() {
		return key;
	}

	public Cheat getCheat() {
		return Cheat.forKey(key);
	}
}
