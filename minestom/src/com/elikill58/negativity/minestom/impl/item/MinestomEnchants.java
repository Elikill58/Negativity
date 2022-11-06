package com.elikill58.negativity.minestom.impl.item;

import com.elikill58.negativity.api.item.Enchantment;

public class MinestomEnchants {
	
	public static net.minestom.server.item.Enchantment getEnchant(Enchantment enchant) {
		return net.minestom.server.item.Enchantment.fromNamespaceId(enchant.getId());
	}
}
