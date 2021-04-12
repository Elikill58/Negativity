package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.location.Location;

public class PlayerMoveEvent implements Event {

	private final Player p;
	private Location from, to;
	private boolean cancel = false, hasToSet = false;
	
	public PlayerMoveEvent(Player p, Location from, Location to) {
		this.p = p;
		this.from = from;
		this.to = to;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Location getTo() {
		return to.clone();
	}
	
	public void setTo(Location loc) {
		this.to = loc;
		hasToSet = true;
	}
	
	public Location getFrom() {
		return from.clone();
	}
	
	public void setFrom(Location loc) {
		this.from = loc;
		hasToSet = true;
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
