package com.elikill58.negativity.common.inventories.holders;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class ReportHolder extends NegativityHolder {
	
	private final Player cible;
	private final int offset;
	
	public ReportHolder(Player cible, int offset) {
		this.cible = cible;
		this.offset = offset;
	}

	public Player getCible() {
		return cible;
	}
	
	public int getOffset() {
		return offset;
	}
}
