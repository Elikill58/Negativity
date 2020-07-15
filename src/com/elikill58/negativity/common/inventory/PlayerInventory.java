package com.elikill58.negativity.common.inventory;

import com.elikill58.negativity.common.item.ItemStack;

public abstract class PlayerInventory extends Inventory {

	public abstract ItemStack[] getArmorContent();
	
	public abstract void setArmorContent(ItemStack[] items);
	
	public abstract int getHeldItemSlot();
}
