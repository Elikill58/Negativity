package com.elikill58.negativity.sponge7.impl.inventory;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemStack;

public class SpongePlayerInventory extends PlayerInventory {

	private final MainPlayerInventory inv;
	private final Player p;
	
	public SpongePlayerInventory(Player p) {
		this.p = p;
		this.inv = p.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
	}
	
	private Optional<ItemStack> getItem(Optional<org.spongepowered.api.item.inventory.ItemStack> opt) {
		return opt.isPresent() ? Optional.of(new SpongeItemStack(opt.get())) : Optional.empty();
	}

	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getItem(p.getHelmet()).orElse(null);
		armor[1] = getItem(p.getChestplate()).orElse(null);
		armor[2] = getItem(p.getLeggings()).orElse(null);
		armor[3] = getItem(p.getBoots()).orElse(null);
		return armor;
	}

	@Override
	public void setArmorContent(ItemStack[] items) {
		p.setHelmet((org.spongepowered.api.item.inventory.ItemStack) items[0].getDefault());
		p.setChestplate((org.spongepowered.api.item.inventory.ItemStack) items[1].getDefault());
		p.setLeggings((org.spongepowered.api.item.inventory.ItemStack) items[2].getDefault());
		p.setBoots((org.spongepowered.api.item.inventory.ItemStack) items[3].getDefault());
	}

	@Override
	public int getHeldItemSlot() {
		return 0;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.get(inv.getArchetype().getId());
	}

	@Override
	public ItemStack get(int slot) {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = this.inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).peek();
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public void set(int slot, ItemStack item) {
		int y = slot / 9;
		int x = slot - (y * 9);
		inv.set(x, y, (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		
	}

	@Override
	public void clear() {
		inv.forEach((i) -> i.set(null));
	}

	@Override
	public void addItem(ItemStack build) {
		inv.offer((org.spongepowered.api.item.inventory.ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inv.capacity();
	}

	@Override
	public String getInventoryName() {
		return inv.getName().get();
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
		p.setHelmet(item == null ? null : (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		p.setChestplate(item == null ? null : (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		p.setLeggings(item == null ? null : (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		p.setBoots(item == null ? null : (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getItem(p.getHelmet());
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getItem(p.getChestplate());
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getItem(p.getLeggings());
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getItem(p.getBoots());
	}
	
	@Override
	public boolean contains(Material type) {
		return inv.contains((ItemType) type.getDefault());
	}
}
