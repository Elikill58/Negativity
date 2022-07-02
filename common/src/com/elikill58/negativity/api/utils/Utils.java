package com.elikill58.negativity.api.utils;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemStack;

public class Utils {
	
	public static boolean isInBoat(Player p) {
		return p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.BOAT);
	}

	public static boolean hasThorns(Player p) {
		ItemStack[] armor = p.getInventory().getArmorContent();
		if(armor == null)
			return false;
		for(ItemStack item : armor)
			if(item != null && item.hasEnchant(Enchantment.THORNS))
				return true;
		return false;
	}

	public static String coloredMessage(String msg) {
		return msg == null ? null : ChatColor.translateAlternateColorCodes('&', msg);
	}
}
