package com.elikill58.negativity.api.item;

import com.elikill58.negativity.api.NegativityObject;

public abstract class ItemStack extends NegativityObject {

	public abstract int getAmount();
	
	public abstract Material getType();
	
	public abstract String getName();
	
	public abstract boolean hasEnchant(Enchantment enchant);
	public abstract int getEnchantLevel(Enchantment enchant);
	public abstract void addEnchant(Enchantment enchant, int level);
	public abstract void removeEnchant(Enchantment enchant);

}
