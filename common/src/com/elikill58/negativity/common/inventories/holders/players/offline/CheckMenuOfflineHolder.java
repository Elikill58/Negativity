package com.elikill58.negativity.common.inventories.holders.players.offline;

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
