package com.elikill58.negativity.spigot.support;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class WorldGuardSupport {

	@SuppressWarnings("deprecation")
	public static boolean isInRegionProtected(Player p) {
		return WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).allows(DefaultFlag.PVP);
	}
}
