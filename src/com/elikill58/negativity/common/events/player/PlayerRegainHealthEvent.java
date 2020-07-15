package com.elikill58.negativity.common.events.player;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;

public class PlayerRegainHealthEvent implements Event {
	
	private final Player p;
	
	public PlayerRegainHealthEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
}
