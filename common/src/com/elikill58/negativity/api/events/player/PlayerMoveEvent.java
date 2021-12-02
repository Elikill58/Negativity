package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.location.Location;

public class PlayerMoveEvent extends PlayerEvent {

	private Location from, to;
	private boolean cancel = false, hasToSet = false;
	
	public PlayerMoveEvent(Player p, Location from, Location to) {
		super(p);
		this.from = from;
		this.to = to;
	}
	
	public Location getTo() {
		return to.clone();
	}
	
	public void setTo(Location to) {
		this.to = to;
		this.hasToSet = true;
	}
	
	public Location getFrom() {
		return from.clone();
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public boolean hasToSet() {
		return hasToSet;
	}
}
