package com.elikill58.negativity.sponge.inventories.holders;

import org.spongepowered.api.entity.living.player.User;

public class CheckMenuOfflineHolder extends NegativityHolder {

	private final User user;
	
	public CheckMenuOfflineHolder(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
}
