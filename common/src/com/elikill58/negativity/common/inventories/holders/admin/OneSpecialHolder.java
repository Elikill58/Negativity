package com.elikill58.negativity.common.inventories.holders.admin;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.universal.Special;

public class OneSpecialHolder extends NegativityHolder {

	private final Special c;

	public OneSpecialHolder(Special c) {
		this.c = c;
	}

	public Special getSpecial() {
		return c;
	}
}
