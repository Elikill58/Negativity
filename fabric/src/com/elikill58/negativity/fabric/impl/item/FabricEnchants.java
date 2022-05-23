package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Enchantment;

import net.minecraft.enchantment.Enchantments;

public class FabricEnchants {
	
	public static net.minecraft.enchantment.Enchantment getFabricEnchant(Enchantment enchant) {
		switch (enchant) {
		case DIG_SPEED:
			return Enchantments.EFFICIENCY;
		case THORNS:
			return Enchantments.THORNS;
		default:
			throw new RuntimeException("Unhandled enchantment " + enchant);
		}
	}
}
