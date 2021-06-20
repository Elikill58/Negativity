package com.elikill58.negativity.api.events.inventory;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class InventoryOpenEvent extends PlayerEvent {

	private boolean cancel = false;
	
	public InventoryOpenEvent(Player p) {
		super(p);
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
