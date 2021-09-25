package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerDamageEntityEvent extends PlayerEvent {

	private boolean cancel;
	private final Entity damaged;
	
	public PlayerDamageEntityEvent(Player player, Entity damaged) {
		super(player);
		this.damaged = damaged;
	}

	public Entity getDamaged() {
		return damaged;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
