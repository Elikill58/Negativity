package com.elikill58.negativity.spigot.support;

import org.bukkit.entity.Player;

public class WorldGuardSupport {
	
	public static boolean isInRegionProtected(Player p) {
		return WorldGuardAPI.isPVPAllowed(p, p.getLocation());
	}
}
