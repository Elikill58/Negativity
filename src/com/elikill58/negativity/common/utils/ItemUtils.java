package com.elikill58.negativity.common.utils;

import com.elikill58.negativity.common.item.Enchantment;
import com.elikill58.negativity.common.item.ItemStack;

public class ItemUtils {


	public static boolean hasDigSpeedEnchant(ItemStack item) {
		return item != null && item.hasEnchant(Enchantment.DIG_SPEED) && item.getEnchantLevel(Enchantment.DIG_SPEED) > 2;
	}
}
