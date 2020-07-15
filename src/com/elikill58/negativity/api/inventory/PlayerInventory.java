package com.elikill58.negativity.api.inventory;

import com.elikill58.negativity.api.item.ItemStack;

public abstract class PlayerInventory extends Inventory {

	public abstract ItemStack[] getArmorContent();
	
	public abstract void setArmorContent(ItemStack[] items);
	
	public abstract int getHeldItemSlot();
}
