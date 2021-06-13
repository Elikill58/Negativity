package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerRegainHealthEvent extends PlayerEvent {

	private boolean cancel = false;
	
	public PlayerRegainHealthEvent(Player p) {
		super(p);
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
