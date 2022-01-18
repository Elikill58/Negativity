package com.elikill58.negativity.sponge.inventories.holders;

import org.spongepowered.api.entity.living.player.Player;

public class CheckMenuHolder extends NegativityHolder {

	private final Player user;
	
	public CheckMenuHolder(Player user) {
		this.user = user;
	}
	
	public Player getPlayer() {
		return user;
	}
}
