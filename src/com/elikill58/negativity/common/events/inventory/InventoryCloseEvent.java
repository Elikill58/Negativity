package com.elikill58.negativity.common.events.inventory;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;

public class InventoryCloseEvent implements Event {

	private final Player p;
	private final InventoryAction action;
	private boolean cancel = false;
	
	public InventoryCloseEvent(Player p, InventoryAction action) {
		this.p = p;
		this.action = action;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public InventoryAction getAction() {
		return action;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
