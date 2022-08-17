package com.elikill58.negativity.common.inventories.holders.players;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class SeeWarnHolder extends NegativityHolder {
	
	private final OfflinePlayer cible;
	private final int page;
	
	public SeeWarnHolder(OfflinePlayer cible, int page) {
		this.cible = cible;
		this.page = page;
	}

	public OfflinePlayer getCible() {
		return cible;
	}
	
	public int getPage() {
		return page;
	}
}
