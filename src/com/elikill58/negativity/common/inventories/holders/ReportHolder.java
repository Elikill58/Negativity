package com.elikill58.negativity.common.inventories.holders;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class ReportHolder extends NegativityHolder {
	
	private final Player cible;
	private final int page;
	
	public ReportHolder(Player cible, int page) {
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
