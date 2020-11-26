package com.elikill58.negativity.sponge8.impl.inventory;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemStack;

public class SpongePlayerInventory extends PlayerInventory {

	private final org.spongepowered.api.item.inventory.entity.PlayerInventory inv;
	private final Player p;
	
	public SpongePlayerInventory(Player p) {
		this.p = p;
		this.inv = p.getInventory();
	}

	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getHelmet();
		armor[1] = getChestplate();
		armor[2] = getLegging();
		armor[3] = getBoots();
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
		return ""; // TODO
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
	public @Nullable ItemStack getHelmet() {
		return getEquipment(EquipmentTypes.HEAD);
	}

	@Override
	public @Nullable ItemStack getChestplate() {
		return getEquipment(EquipmentTypes.CHEST);
	}

	@Override
	public @Nullable ItemStack getLegging() {
		return getEquipment(EquipmentTypes.LEGS);
	}

	@Override
	public @Nullable ItemStack getBoots() {
		return getEquipment(EquipmentTypes.FEET);
	}
	
	private void setEquipment(Supplier<EquipmentType> equipment, @Nullable ItemStack stack) {
		p.equip(equipment, nonNullStack(stack));
	}
	
	private org.spongepowered.api.item.inventory.ItemStack nonNullStack(@Nullable ItemStack stack) {
		return stack == null ? org.spongepowered.api.item.inventory.ItemStack.empty() : ((org.spongepowered.api.item.inventory.ItemStack) stack.getDefault());
	}
	
	private @Nullable ItemStack getEquipment(Supplier<EquipmentType> equipment) {
		return nonEmptyOrNull(p.getEquipped(equipment.get()).orElse(null));
	}
	
	private @Nullable ItemStack nonEmptyOrNull(org.spongepowered.api.item.inventory.@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() ? new SpongeItemStack(stack) : null;
	}
}
