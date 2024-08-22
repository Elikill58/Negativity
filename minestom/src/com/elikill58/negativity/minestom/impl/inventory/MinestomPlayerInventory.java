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

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;

public class MinestomPlayerInventory extends PlayerInventory {

	private final net.minestom.server.inventory.PlayerInventory inv;
	
	public MinestomPlayerInventory(net.minestom.server.inventory.PlayerInventory inv) {
		this.inv = inv;
	}
	
	public MinestomPlayerInventory(Player p) {
		this.inv = p.getInventory();
	}

	private Optional<ItemStack> getArmorItem(EquipmentSlot slot) {
		net.minestom.server.item.ItemStack i = inv.getEquipment(slot, slot.armorSlot());
		return i == null || i.material().equals(net.minestom.server.item.Material.AIR) ? Optional.empty() : Optional.of(new MinestomItemStack(i));
	}
	
	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getArmorItem(EquipmentSlot.HELMET).orElse(null);
		armor[1] = getArmorItem(EquipmentSlot.CHESTPLATE).orElse(null);
		armor[2] = getArmorItem(EquipmentSlot.LEGGINGS).orElse(null);
		armor[3] = getArmorItem(EquipmentSlot.BOOTS).orElse(null);
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
	
	private void setArmorItem(EquipmentSlot slot, ItemStack item) {
		inv.setEquipment(slot, slot.armorSlot(), (net.minestom.server.item.@NotNull ItemStack) item.getDefault());
	}

	@Override
	public void setHelmet(@Nullable ItemStack item) {
		setArmorItem(EquipmentSlot.HELMET, item);
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		setArmorItem(EquipmentSlot.CHESTPLATE, item);
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		setArmorItem(EquipmentSlot.LEGGINGS, item);
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		setArmorItem(EquipmentSlot.BOOTS, item);
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getArmorItem(EquipmentSlot.HELMET);
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getArmorItem(EquipmentSlot.CHESTPLATE);
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getArmorItem(EquipmentSlot.LEGGINGS);
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getArmorItem(EquipmentSlot.BOOTS);
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
