package com.elikill58.negativity.api.inventory;

import java.util.Optional;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.universal.annotations.Nullable;

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
	 * Edit the helmet armor.
	 * Set null to remove it.
	 * 
	 * @param item the new helmet
	 */
	public abstract void setHelmet(@Nullable ItemStack item);
	/**
	 * Edit the chestplate armor.
	 * Set null to remove it.
	 * 
	 * @param item the new chestplate
	 */
	public abstract void setChestplate(@Nullable ItemStack item);
	/**
	 * Edit the legging armor.
	 * Set null to remove it.
	 * 
	 * @param item the new legging
	 */
	public abstract void setLegging(@Nullable ItemStack item);
	/**
	 * Edit the boot armor.
	 * Set null to remove it.
	 * 
	 * @param item the new boot
	 */
	public abstract void setBoot(@Nullable ItemStack item);
	
	/**
	 * Get current helmet.
	 * 
	 * @return the current helmet
	 */
	@Nullable
	public abstract Optional<ItemStack> getHelmet();
	/**
	 * Get current chestplate.
	 * 
	 * @return the current chestplate
	 */
	@Nullable
	public abstract Optional<ItemStack> getChestplate();
	/**
	 * Get current legging.
	 * 
	 * @return the current legging
	 */
	@Nullable
	public abstract Optional<ItemStack> getLegging();
	/**
	 * Get current boot.
	 * 
	 * @return the current boot
	 */
	public abstract Optional<ItemStack> getBoots();
	
	/**
	 * Get slot of the hold item by the player
	 * 
	 * @return the hold slot
	 */
	public abstract int getHeldItemSlot();
}
