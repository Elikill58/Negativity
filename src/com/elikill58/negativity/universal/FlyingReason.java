package com.elikill58.negativity.universal;

public enum FlyingReason {
	POTION(Cheat.fromString(CheatKeys.ANTI_POTION)), REGEN(Cheat.fromString(CheatKeys.REGEN)), EAT(
			Cheat.fromString(CheatKeys.FAST_EAT)), BOW(Cheat.fromString(CheatKeys.FAST_BOW));

	private Cheat c;

	FlyingReason(Cheat c) {
		this.c = c;
	}

	public Cheat getCheat() {
		return c;
	}

}
