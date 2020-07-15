package com.elikill58.negativity.common.events.player;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;

public class PlayerDeathEvent implements Event {

	private final Player p;
	
	public PlayerDeathEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
}
