package com.elikill58.negativity.spigot.support;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

public class EssentialsSupport {

	private static Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

	public static boolean checkEssentialsPrecondition(Player p) {
		return essentials.getUser(p).isGodModeEnabled();
	}
	
	public static boolean checkEssentialsSpeedPrecondition(Player p) {
		if(p.hasPermission("essentials.speed"))
			return true;
		else
			return false;
	}
}
