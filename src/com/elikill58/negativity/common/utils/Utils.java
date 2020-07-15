package com.elikill58.negativity.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.common.ChatColor;
import com.elikill58.negativity.common.entity.EntityType;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.item.Enchantment;
import com.elikill58.negativity.common.item.ItemStack;

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
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static List<String> coloredMessage(String... messages) {
		List<String> ret = new ArrayList<>();
		for (String message : messages) {
			ret.add(coloredMessage(message));
		}
		return ret;
	}
}
