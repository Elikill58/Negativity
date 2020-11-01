package com.elikill58.negativity.api.item;

import com.elikill58.negativity.api.NegativityObject;

public abstract class ItemStack extends NegativityObject {

	/**
	 * Get amount of the item
	 * 
	 * @return item amount
	 */
	public abstract int getAmount();
	
	/**
	 * Get the item material type
	 * 
	 * @return the material
	 */
	public abstract Material getType();
	
	/**
	 * Get the display name of item
	 * null if the item doesn't have any name
	 * 
	 * @return the name
	 */
	public abstract String getName();
	
	public abstract boolean hasEnchant(Enchantment enchant);
	public abstract int getEnchantLevel(Enchantment enchant);
	public abstract void addEnchant(Enchantment enchant, int level);
	public abstract void removeEnchant(Enchantment enchant);

	@Override
	public String toString() {
		return "ItemStack{type=" + getType().getId() + ",amount=" + getAmount() + ",name=" + getName() + "}";
	}
}
