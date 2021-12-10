package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.item.ItemStack;

public class PlayerItemConsumeEvent extends PlayerEvent implements CancellableEvent {

	private final ItemStack item;
	private boolean cancel = false;
	
	public PlayerItemConsumeEvent(Player p, ItemStack item) {
		super(p);
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
