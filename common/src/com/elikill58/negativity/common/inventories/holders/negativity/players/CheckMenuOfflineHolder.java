package com.elikill58.negativity.common.inventories.holders.negativity.players;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class CheckMenuOfflineHolder extends NegativityHolder {
	
	private final OfflinePlayer cible;
	
	public CheckMenuOfflineHolder(OfflinePlayer cible) {
		this.cible = cible;
	}

	public OfflinePlayer getCible() {
		return cible;
	}
}
