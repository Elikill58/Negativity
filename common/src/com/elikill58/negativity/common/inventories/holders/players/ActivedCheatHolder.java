package com.elikill58.negativity.common.inventories.holders.players;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.NegativityHolder;

public class ActivedCheatHolder extends NegativityHolder {

	private final Player cible;
	
	public ActivedCheatHolder(Player cible) {
		this.cible = cible;
	}

	public Player getCible() {
		return cible;
	}
	
}
