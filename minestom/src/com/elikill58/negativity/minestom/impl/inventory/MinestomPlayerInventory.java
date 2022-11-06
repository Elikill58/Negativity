package com.elikill58.negativity.minestom.impl.inventory;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.minestom.impl.inventory.holders.MinestomNegativityHolder;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;

import net.minestom.server.entity.Player;

public class MinestomPlayerInventory extends PlayerInventory {

	private final net.minestom.server.inventory.PlayerInventory inv;
	
	public MinestomPlayerInventory(net.minestom.server.inventory.PlayerInventory inv) {
		this.inv = inv;
	}
	
	public MinestomPlayerInventory(Player p) {
		this.inv = p.getInventory();
	}

	private ItemStack getItem(net.minestom.server.item.ItemStack i) {
		return i == null || i.material().equals(net.minestom.server.item.Material.AIR) ? null : new MinestomItemStack(i);
	}

	private Optional<ItemStack> getItemOpt(net.minestom.server.item.ItemStack i) {
		return i == null || i.material().equals(net.minestom.server.item.Material.AIR) ? Optional.empty() : Optional.of(new MinestomItemStack(i));
	}
	
	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getItem(inv.getHelmet());
		armor[1] = getItem(inv.getChestplate());
		armor[2] = getItem(inv.getLeggings());
		armor[3] = getItem(inv.getBoots());
		return armor;
	}

	@Override
	public void setArmorContent(ItemStack[] items) {
		setHelmet(items[0]);
		setChestplate(items[0]);
		setLegging(items[0]);
		setBoot(items[0]);
	}

	@Override
	public int getHeldItemSlot() {
		return 0;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.PLAYER;
	}

	@Override
	public ItemStack get(int slot) {
		return new MinestomItemStack(inv.getItemStack(slot));
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setItemStack(slot, (net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		inv.setItemStack(slot, net.minestom.server.item.ItemStack.AIR);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.addItemStack((net.minestom.server.item.@NotNull ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inv.getSize();
	}

	@Override
	public String getInventoryName() {
		return inv.toString();
	}

	@Override
	public NegativityHolder getHolder() {
		return new MinestomNegativityHolder();
	}

	@Override
	public Object getDefault() {
		return inv;
	}

	@Override
	public void setHelmet(@Nullable ItemStack item) {
		inv.setHelmet((net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		inv.setChestplate((net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		inv.setLeggings((net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		inv.setBoots((net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getItemOpt(inv.getHelmet());
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getItemOpt(inv.getChestplate());
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getItemOpt(inv.getLeggings());
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getItemOpt(inv.getBoots());
	}
	
	@Override
	public boolean contains(Material type) {
		net.minestom.server.item.Material m = (net.minestom.server.item.Material) type.getDefault();
		for(net.minestom.server.item.ItemStack i : inv.getItemStacks()) {
			if(i.material().equals(m))
				return true;
		}
		return false;
	}
}
