package com.elikill58.negativity.common.inventories.holders.admin.detections;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.universal.detections.Cheat;

public class OneCheatHolder extends NegativityHolder {

	private final Cheat c;

	public OneCheatHolder(Cheat c) {
		this.c = c;
	}

	public Cheat getCheat() {
		return c;
	}
}
