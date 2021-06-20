package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerDeathEvent extends PlayerEvent {

	public PlayerDeathEvent(Player p) {
		super(p);
	}
	
}
