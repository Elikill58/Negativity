package com.elikill58.negativity.api.utils;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemStack;

public class ItemUtils {


	public static boolean hasDigSpeedEnchant(ItemStack item) {
		return item != null && item.getEnchantLevel(Enchantment.EFFICIENCY) > 2;
	}
}
