package com.elikill58.negativity.spigot.impl.inventory;

import org.bukkit.Bukkit;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

public class SpigotInventory extends Inventory {

	private final String inventoryName;
	private final int size;
	private final SpigotNegativityHolder holder;
	private final org.bukkit.inventory.Inventory inv;
	
	public SpigotInventory(org.bukkit.inventory.Inventory inv) {
		this.inventoryName = "";
		this.size = inv.getSize();
		if(inv.getHolder() instanceof SpigotNegativityHolder) {
			this.holder = (SpigotNegativityHolder) inv.getHolder();
		} else
			this.holder = (inv.getHolder() == null ? null : new SpigotNegativityHolder(new SpigotInventoryHolder(inv.getHolder())));
		this.inv = inv;
	}
	
	public SpigotInventory(String inventoryName, int size, NegativityHolder holder) {
		this.inventoryName = inventoryName;
		this.size = size;
		this.holder = new SpigotNegativityHolder(holder);
		this.inv = Bukkit.createInventory(this.holder, size, inventoryName);
	}
	
	@Override
	public ItemStack get(int slot) {
		org.bukkit.inventory.ItemStack item = inv.getItem(slot);
		if(item == null)
			return null;
		return new SpigotItemStack(item);
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setItem(slot, (org.bukkit.inventory.ItemStack) item.getDefaultItem());
	}

	@Override
	public void remove(int slot) {
		inv.setItem(slot, null);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String getInventoryName() {
		return inventoryName;
	}

	@Override
	public PlatformHolder getHolder() {
		return holder.getBasicHolder();
	}

	@Override
	public Object getDefaultInventory() {
		return inv;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.valueOf(inv.getType().name());
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.addItem((org.bukkit.inventory.ItemStack) build.getDefaultItem());
	}

}
