package com.elikill58.negativity.api.events.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.ItemStack;

public class InventoryClickEvent implements Event {

	private final Player p;
	private final InventoryAction action;
	private final int slot;
	private final ItemStack item;
	private final Inventory inv;
	private boolean cancel = false;
	
	public InventoryClickEvent(Player p, InventoryAction action, int slot, ItemStack item, Inventory inv) {
		this.p = p;
		this.action = action;
		this.slot = slot;
		this.item = item;
		this.inv = inv;
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
	
	public Inventory getClickedInventory() {
		return inv;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
