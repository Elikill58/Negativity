package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerDamageByEntityEvent extends PlayerEvent {

	private boolean cancel;
	private final Player cible;
	private final Entity damager;
	
	public PlayerDamageByEntityEvent(Player cible, Entity damager) {
		super(cible);
		this.cible = cible;
		this.damager = damager;
	}

	public Player getEntity() {
		return cible;
	}

	public Entity getDamager() {
		return damager;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
