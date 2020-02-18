package com.elikill58.negativity.universal;

public enum FlyingReason {
	POTION(Cheat.fromString(CheatKeys.ANTI_POTION).get()), REGEN(Cheat.fromString(CheatKeys.REGEN).get()), EAT(
			Cheat.fromString(CheatKeys.FAST_EAT).get()), BOW(Cheat.fromString(CheatKeys.FAST_BOW).get());

	private Cheat c;

	FlyingReason(Cheat c) {
		this.c = c;
	}

	public Cheat getCheat() {
		return c;
	}

}
