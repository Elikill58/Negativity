package com.elikill58.negativity.sponge.inventories.holders;

import com.elikill58.negativity.universal.Cheat;

public class OneCheatHolder extends NegativityHolder {

	private final Cheat c;
	
	public OneCheatHolder(Cheat c) {
		this.c = c;
	}
	
	public Cheat getCheat() {
		return c;
	}
}
