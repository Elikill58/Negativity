package com.elikill58.negativity.spigot.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.universal.Version;

@SuppressWarnings("deprecation")
public class HandUtils {

	public static boolean handUseItem(Player p, String itemName) {
		return itemIsType(p.getItemInHand(), itemName) || (Version.getVersion().isNewerOrEquals(Version.V1_9)
				&& itemIsType(p.getInventory().getItemInOffHand(), itemName));
	}

	private static boolean itemIsType(ItemStack item, String name) {
		return item != null && item.getType().name().contains(name);
	}

	public static boolean handHasEnchant(Player p, Enchantment enchant) {
		return p.getItemInHand().containsEnchantment(enchant) || (Version.getVersion().isNewerOrEquals(Version.V1_9)
				&& p.getInventory().getItemInOffHand().containsEnchantment(enchant));
	}
}
