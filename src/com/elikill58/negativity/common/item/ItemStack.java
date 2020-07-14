package com.elikill58.negativity.common.item;

public abstract class ItemStack {

	public abstract int getAmount();
	
	public abstract Material getType();
	
	public abstract String getName();
	
	public abstract boolean hasEnchant(Enchantment enchant);
	public abstract int getEnchantLevel(Enchantment enchant);
	public abstract void addEnchant(Enchantment enchant, int level);
	public abstract void removeEnchant(Enchantment enchant);
}
