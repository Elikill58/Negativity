package com.elikill58.negativity.common.events.player;

import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;

public class PlayerDamageByEntityEvent implements Event {

	private boolean cancel;
	private final Player cible;
	private final Entity damager;
	
	public PlayerDamageByEntityEvent(Player cible, Entity damager) {
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
