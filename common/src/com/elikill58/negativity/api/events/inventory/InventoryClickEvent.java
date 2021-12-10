package com.elikill58.negativity.api.events.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.ItemStack;

public class InventoryClickEvent extends PlayerEvent implements CancellableEvent {

	private final InventoryAction action;
	private final int slot;
	private final ItemStack item;
	private final Inventory inv;
	private boolean cancel = false;
	
	public InventoryClickEvent(Player p, InventoryAction action, int slot, ItemStack item, Inventory inv) {
		super(p);
		this.action = action;
		this.slot = slot;
		this.item = item;
		this.inv = inv;
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

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
