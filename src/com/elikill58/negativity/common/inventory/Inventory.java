package com.elikill58.negativity.common.inventory;

import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class Inventory {
	
	public abstract InventoryType getType();
	
	public abstract ItemStack get(int slot);

	public abstract void set(int slot, ItemStack item);
	
	public abstract void remove(int slot);
	
	public abstract void clear();

	public abstract void addItem(ItemStack build);
	
	public abstract int getSize();
	
	public abstract String getInventoryName();
	
	public abstract NegativityHolder getHolder();

	public abstract Object getDefaultInventory();
	
	public static Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return Adapter.getAdapter().createInventory(inventoryName, size, holder);
	}
}
