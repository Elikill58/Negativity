package com.elikill58.negativity.api.inventory;

import com.elikill58.negativity.api.item.ItemStack;

public abstract class PlayerInventory extends Inventory {

	/**
	 * Get the armor of the player
	 * 0: helmet
	 * 1: chestplate
	 * 2: legging
	 * 3: boot
	 * Each index can be null if the player doesn't have any armor
	 * 
	 * @return the array of armor
	 */
	public abstract ItemStack[] getArmorContent();
	
	/**
	 * Set a new armor of the player
	 * 0: helmet
	 * 1: chestplate
	 * 2: legging
	 * 3: boot
	 * Each index can be null if the player doesn't have any armor
	 * 
	 * 
	 * @param items have to contains 4 items for all armor slot
	 */
	public abstract void setArmorContent(ItemStack[] items);
	
	/**
	 * Get slot of the hold item by the player
	 * 
	 * @return the hold slot
	 */
	public abstract int getHeldItemSlot();
}
