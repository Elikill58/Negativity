package com.elikill58.negativity.api.events.inventory;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.inventory.Inventory;

public class InventoryCloseEvent implements Event {

	private final Player p;
	private final Inventory inv;
	private boolean cancel = false;
	
	public InventoryCloseEvent(Player p, Inventory inv) {
		this.p = p;
		this.inv = inv;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Inventory getInventory() {
		return inv;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
