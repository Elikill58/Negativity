package com.elikill58.negativity.sponge9.impl.inventory;

import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge9.impl.item.SpongeItemStack;

public class SpongePlayerInventory extends PlayerInventory {

	private final org.spongepowered.api.item.inventory.entity.PlayerInventory inv;
	private final ServerPlayer p;
	
	public SpongePlayerInventory(ServerPlayer p) {
		this.p = p;
		this.inv = p.inventory();
	}

	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getHelmet().orElse(null);
		armor[1] = getChestplate().orElse(null);
		armor[2] = getLegging().orElse(null);
		armor[3] = getBoots().orElse(null);
		return armor;
	}

	@Override
	public void setArmorContent(ItemStack[] items) {
		setHelmet(items[0]);
		setChestplate(items[1]);
		setLegging(items[2]);
		setBoot(items[3]);
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
	public @Nullable ItemStack get(int slot) {
		return nonEmptyOrNull(inv.peekAt(slot).orElse(null));
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.set(slot, (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
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
		inv.offer((org.spongepowered.api.item.inventory.ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inv.capacity();
	}

	@Override
	public String getInventoryName() {
		return "";
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
		setEquipment(EquipmentTypes.HEAD, item);
	}

	@Override
	public void setChestplate(@Nullable ItemStack item) {
		setEquipment(EquipmentTypes.CHEST, item);
	}

	@Override
	public void setLegging(@Nullable ItemStack item) {
		setEquipment(EquipmentTypes.LEGS, item);
	}

	@Override
	public void setBoot(@Nullable ItemStack item) {
		setEquipment(EquipmentTypes.FEET, item);
	}

	@Override
	public Optional<ItemStack> getHelmet() {
		return getEquipment(EquipmentTypes.HEAD);
	}

	@Override
	public Optional<ItemStack> getChestplate() {
		return getEquipment(EquipmentTypes.CHEST);
	}

	@Override
	public Optional<ItemStack> getLegging() {
		return getEquipment(EquipmentTypes.LEGS);
	}

	@Override
	public Optional<ItemStack> getBoots() {
		return getEquipment(EquipmentTypes.FEET);
	}
	
	private void setEquipment(Supplier<EquipmentType> equipment, @Nullable ItemStack stack) {
		p.equip(equipment, nonNullStack(stack));
	}
	
	private org.spongepowered.api.item.inventory.ItemStack nonNullStack(@Nullable ItemStack stack) {
		return stack == null ? org.spongepowered.api.item.inventory.ItemStack.empty() : ((org.spongepowered.api.item.inventory.ItemStack) stack.getDefault());
	}
	
	private Optional<ItemStack> getEquipment(Supplier<EquipmentType> equipment) {
		org.spongepowered.api.item.inventory.ItemStack spongeItem = p.equipped(equipment.get()).orElse(null);
		return spongeItem == null || spongeItem.isEmpty() ? Optional.empty() : Optional.of(new SpongeItemStack(spongeItem));
	}
	
	private @Nullable ItemStack nonEmptyOrNull(org.spongepowered.api.item.inventory.@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() ? new SpongeItemStack(stack) : null;
	}

	@Override
	public boolean contains(Material type) {
		return inv.contains((org.spongepowered.api.item.ItemType) type.getDefault());
	}
}
