package com.elikill58.negativity.common.events.inventory;

import javax.annotation.Nullable;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.common.item.ItemStack;

public class InventoryClickEvent implements Event {

	private final Player p;
	private final InventoryAction action;
	private final int slot;
	private final ItemStack item;
	private boolean cancel = false;
	
	public InventoryClickEvent(Player p, InventoryAction action, int slot, ItemStack item) {
		this.p = p;
		this.action = action;
		this.slot = slot;
		this.item = item;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public InventoryAction getAction() {
		return action;
	}

	public int getSlot() {
		return slot;
	}
	
	@Nullable
	public ItemStack getCurrentItem() {
		return item;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
