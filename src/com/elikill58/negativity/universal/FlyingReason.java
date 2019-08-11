package com.elikill58.negativity.universal;

public enum FlyingReason {
	POTION(Cheat.fromString("ANTIPOTION").get()), REGEN(Cheat.fromString("AUTOREGEN").get()), EAT(
			Cheat.fromString("AUTOEAT").get()), BOW(Cheat.fromString("FASTBOW").get());

	private Cheat c;

	FlyingReason(Cheat c) {
		this.c = c;
	}

	public Cheat getCheat() {
		return c;
	}

}
