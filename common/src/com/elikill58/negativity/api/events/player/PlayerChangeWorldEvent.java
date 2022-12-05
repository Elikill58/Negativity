package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.location.World;

public class PlayerChangeWorldEvent extends PlayerEvent {

	private World serverWorld;
	
	public PlayerChangeWorldEvent(Player p, World next) {
		super(p);
		this.serverWorld = next;
	}
	
	/**
	 * Get next world server
	 * 
	 * @return next world
	 */
	public World getServerWorld() {
		return serverWorld;
	}
}
