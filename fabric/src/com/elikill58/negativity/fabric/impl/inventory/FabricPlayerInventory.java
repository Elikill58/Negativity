package com.elikill58.negativity.fabric.impl.inventory;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricPlayerInventory extends PlayerInventory {

	private final net.minecraft.entity.player.PlayerInventory inv;
	
	public FabricPlayerInventory(ServerPlayerEntity p) {
		this.inv = p.getInventory();
	}

	private ItemStack getItem(net.minecraft.item.ItemStack i) {
		return i == null ? null : new FabricItemStack(i);
	}

	private Optional<ItemStack> getItemOpt(net.minecraft.item.ItemStack i) {
		return i == null ? Optional.empty() : Optional.of(new FabricItemStack(i));
	}
	
	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getItem(inv.getArmorStack(0));
		armor[1] = getItem(inv.getArmorStack(1));
		armor[2] = getItem(inv.getArmorStack(2));
		armor[3] = getItem(inv.getArmorStack(3));
		return armor;
	}

	@Override
	public void setArmorContent(ItemStack[] items) {
		for(int i = 0; i < items.length; i++)
			inv.armor.set(i, items[i] == null ? null : (net.minecraft.item.ItemStack) items[i].getDefault());
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
		net.minecraft.item.ItemStack opt = inv.getStack(slot);
		return opt != null ? new FabricItemStack(opt) : null;
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setStack(slot, (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.insertStack((net.minecraft.item.ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inv.size();
	}

	@Override
	public String getInventoryName() {
		return inv.getName().getString();
	}

	@Override
	public @Nullable NegativityHolder getHolder() {
		return null;
	}

	@Override
	public Object getDefault() {
		return inv;
	}

	@Override
	public void setHelmet(@Nullable ItemStack item) {
		inv.armor.set(0, item == null ? null : (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		inv.armor.set(1, item == null ? null : (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		inv.armor.set(2, item == null ? null : (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		inv.armor.set(3, item == null ? null : (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getItemOpt(inv.getArmorStack(0));
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getItemOpt(inv.getArmorStack(1));
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getItemOpt(inv.getArmorStack(2));
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getItemOpt(inv.getArmorStack(3));
	}
	
	@Override
	public boolean contains(Material type) {
		return inv.contains(new net.minecraft.item.ItemStack(((Item) type.getDefault()).asItem()));
	}
}
