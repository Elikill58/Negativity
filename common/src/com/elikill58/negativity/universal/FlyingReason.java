package com.elikill58.negativity.universal;

public enum FlyingReason {
	POTION(Cheat.forKey(CheatKeys.ANTI_POTION)), REGEN(Cheat.forKey(CheatKeys.REGEN)), EAT(
			Cheat.forKey(CheatKeys.FAST_EAT)), BOW(Cheat.forKey(CheatKeys.FAST_BOW));

	private Cheat c;

	FlyingReason(Cheat c) {
		this.c = c;
	}

	public Cheat getCheat() {
		return c;
	}

}
