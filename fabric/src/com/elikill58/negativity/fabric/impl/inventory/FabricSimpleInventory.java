package com.elikill58.negativity.fabric.impl.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.include.com.google.common.collect.Sets;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.fabric.impl.inventory.holders.FabricNegativityHolder;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;

public class FabricSimpleInventory extends Inventory {

	private final SimpleInventory inv;
	private final String name;
	private NegativityHolder holder;

	public FabricSimpleInventory(String inventoryName, int size, NegativityHolder holder) {
		this.holder = holder;
		this.name = inventoryName;
		this.inv = new SimpleInventory(size);
	}

	@Override
	public InventoryType getType() {
		return InventoryType.UNKNOW;
	}

	@Override
	public ItemStack get(int slot) {
		return new FabricItemStack(inv.getStack(slot));
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setStack(slot, (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		inv.removeStack(slot);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.addStack((net.minecraft.item.ItemStack) build.getDefault());

	}

	@Override
	public int getSize() {
		return inv.size();
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public @Nullable PlatformHolder getHolder() {
		return new FabricNegativityHolder(holder);
	}

	@Override
	public Object getDefault() {
		return inv;
	}

	@Override
	public boolean contains(Material type) {
		return inv.containsAny(Sets.newHashSet((Item) type.getDefault()));
	}
}
