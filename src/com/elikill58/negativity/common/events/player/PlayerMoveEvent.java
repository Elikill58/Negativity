package com.elikill58.negativity.common.events.player;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.common.location.Location;

public class PlayerMoveEvent implements Event {

	private final Player p;
	private Location from, to;
	private boolean cancel;
	
	public PlayerMoveEvent(Player p, Location from, Location to) {
		this.p = p;
		this.from = from;
		this.to = to;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Location getTo() {
		return to;
	}
	
	public void setTo(Location loc) {
		this.to = loc;
	}
	
	public Location getFrom() {
		return from;
	}
	
	public void setFrom(Location loc) {
		this.from = loc;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
