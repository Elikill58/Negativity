package com.elikill58.negativity.sponge.impl.inventory;

import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;

public class SpongePlayerInventory extends PlayerInventory {

	private final CarriedInventory<? extends Carrier> inventory;
	
	public SpongePlayerInventory(CarriedInventory<? extends Carrier> inventory) {
		this.inventory = inventory;
	}

	@Override
	public ItemStack[] getArmorContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArmorContent(ItemStack[] items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHeldItemSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.valueOf(inventory.getArchetype().getId().toUpperCase());
	}

	@Override
	public ItemStack get(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(int slot, ItemStack item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(int slot) {
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addItem(ItemStack build) {
		inventory.offer((org.spongepowered.api.item.inventory.ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inventory.capacity();
	}

	@Override
	public String getInventoryName() {
		return inventory.getName().get();
	}

	@Override
	public PlatformHolder getHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDefault() {
		return inventory;
	}
	
}
