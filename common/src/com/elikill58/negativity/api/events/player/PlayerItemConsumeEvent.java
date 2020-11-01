package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.item.ItemStack;

public class PlayerItemConsumeEvent implements Event {

	private final Player p;
	private final ItemStack item;
	private boolean cancel = false;
	
	public PlayerItemConsumeEvent(Player p, ItemStack item) {
		this.p = p;
		this.item = item;
	}
	
	public Player getPlayer() {
		return p;
	}

	public ItemStack getItem() {
		return item;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
