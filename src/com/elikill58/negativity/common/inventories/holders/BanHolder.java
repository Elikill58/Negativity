package com.elikill58.negativity.common.inventories.holders;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class BanHolder extends NegativityHolder {
	
	private final Player cible;
	
	public BanHolder(Player cible) {
		this.cible = cible;
	}

	public Player getCible() {
		return cible;
	}
}
