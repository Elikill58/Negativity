package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Enchantment;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FabricEnchants {
	
	public static net.minecraft.enchantment.Enchantment getFabricEnchant(Enchantment enchant) {
		return Registry.ENCHANTMENT.get(new Identifier(enchant.getId()));
	}
}
