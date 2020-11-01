package com.elikill58.negativity.common.inventories.holders.negativity.players;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class SeeReportHolder extends NegativityHolder {
	
	private final Player cible;
	private final int page;
	
	public SeeReportHolder(Player cible, int page) {
		this.cible = cible;
		this.page = page;
	}

	public Player getCible() {
		return cible;
	}
	
	public int getPage() {
		return page;
	}
}
