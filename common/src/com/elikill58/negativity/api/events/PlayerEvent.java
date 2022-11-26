package com.elikill58.negativity.api.events;

import com.elikill58.negativity.api.entity.Player;

public abstract class PlayerEvent implements Event {

	private final Player p;
	
	public PlayerEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public boolean hasPlayer() {
		return p != null;
	}
}
