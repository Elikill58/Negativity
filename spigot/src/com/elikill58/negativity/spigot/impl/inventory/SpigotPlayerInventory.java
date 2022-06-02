package com.elikill58.negativity.spigot.impl.inventory;

import java.util.Optional;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.universal.annotations.Nullable;

public class SpigotPlayerInventory extends PlayerInventory {

	private final org.bukkit.inventory.PlayerInventory inv;
	
	public SpigotPlayerInventory(org.bukkit.inventory.PlayerInventory inv) {
		this.inv = inv;
	}
	
	private Optional<ItemStack> getItem(org.bukkit.inventory.ItemStack item) {
		return item == null ? Optional.empty() : Optional.of(new SpigotItemStack(item));
	}
	
	@Override
	public ItemStack get(int slot) {
		return getItem(inv.getItem(slot)).orElse(null);
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
	public @Nullable NegativityHolder getHolder() {
		return null;
	}

	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] items = new ItemStack[inv.getArmorContents().length];
		int i = 0;
		for(org.bukkit.inventory.ItemStack tempItem : inv.getArmorContents()) {
			items[i] = getItem(tempItem).orElse(null);
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

	@Override
	public void setHelmet(@Nullable ItemStack item) {
		inv.setHelmet(item == null ? null : (org.bukkit.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		inv.setChestplate(item == null ? null : (org.bukkit.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		inv.setLeggings(item == null ? null : (org.bukkit.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		inv.setBoots(item == null ? null : (org.bukkit.inventory.ItemStack) item.getDefault());
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getItem(inv.getHelmet());
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getItem(inv.getChestplate());
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getItem(inv.getLeggings());
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getItem(inv.getBoots());
	}
	
	@Override
	public boolean contains(Material type) {
		return inv.contains((org.bukkit.Material) type.getDefault());
	}
}
