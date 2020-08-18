package com.elikill58.negativity.spigot.impl.inventory;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

public class SpigotPlayerInventory extends PlayerInventory {

	private final org.bukkit.inventory.PlayerInventory inv;
	
	public SpigotPlayerInventory(org.bukkit.inventory.PlayerInventory inv) {
		this.inv = inv;
	}
	
	@Override
	public ItemStack get(int slot) {
		return new SpigotItemStack(inv.getItem(slot));
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setItem(slot, (org.bukkit.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		inv.setItem(slot, null);
	}

	@Override
	public int getSize() {
		return inv.getSize();
	}

	@Override
	public String getInventoryName() {
		return inv.getHolder().getName();
	}

	@Override
	public NegativityHolder getHolder() {
		return null;
	}

	@Override
	public ItemStack[] getArmorContent() {
		SpigotItemStack[] items = new SpigotItemStack[inv.getArmorContents().length];
		int i = 0;
		for(org.bukkit.inventory.ItemStack tempItem : inv.getArmorContents()) {
			if(tempItem == null)
				items[i] = null;
			else
				items[i] = new SpigotItemStack(tempItem);
			i++;
		}
		return items;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.PLAYER;
	}

	@Override
	public void clear() {
		inv.clear();
	}
	
	@Override
	public void setArmorContent(ItemStack[] items) {
		int size = inv.getArmorContents().length;
		org.bukkit.inventory.ItemStack[] armor = new org.bukkit.inventory.ItemStack[size];
		for(int i = 0; i < size; i++) {
			if(items == null || items.length < i)
				armor[i] = null;
			else
				armor[i] = (org.bukkit.inventory.ItemStack) items[i].getDefault();
		}
		inv.setArmorContents(armor);
	}

	@Override
	public int getHeldItemSlot() {
		return inv.getHeldItemSlot();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.addItem((org.bukkit.inventory.ItemStack) build.getDefault());
	}

	@Override
	public Object getDefault() {
		return inv;
	}
}
