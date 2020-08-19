package com.elikill58.negativity.sponge.impl.inventory;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;

public class SpongePlayerInventory extends PlayerInventory {

	private final CarriedInventory<? extends Carrier> inv;
	private final GridInventory invGrid;
	private final Player p;
	
	public SpongePlayerInventory(Player p, CarriedInventory<? extends Carrier> inventory) {
		this.p = p;
		this.inv = inventory;
		this.invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
	}
	
	private ItemStack getItem(Optional<org.spongepowered.api.item.inventory.ItemStack> opt) {
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public ItemStack[] getArmorContent() {
		ItemStack[] armor = new ItemStack[4];
		armor[0] = getItem(p.getHelmet());
		armor[1] = getItem(p.getChestplate());
		armor[2] = getItem(p.getLeggings());
		armor[3] = getItem(p.getBoots());
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
		return InventoryType.valueOf(inv.getArchetype().getId().toUpperCase());
	}

	@Override
	public ItemStack get(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(int slot, ItemStack item) {
		int y = (int) slot / 9;
		int x = slot - (y * 9);
		invGrid.set(x, y, (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
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
	public NegativityHolder getHolder() {
		return null;
	}

	@Override
	public Object getDefault() {
		return inv;
	}
	
}
