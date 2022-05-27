package com.elikill58.negativity.spigot.integration.worldguard.v7;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.Adapter;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardV7Support {
	
	private static final WorldGuardPlugin WORLDGUARD_PLUGIN = (WorldGuardPlugin) Adapter.getAdapter().getPlugin("WorldGuard").getDefault();
	
	public static boolean isInRegionProtected(Player p) {
		return isPVPAllowed(p, p.getLocation());
	}

	public static boolean isPVPAllowed(Player player, Location location) {
		ApplicableRegionSet checkSet = getRegionSet(location);
		if (checkSet == null)
			return true;

		return checkSet.queryState(WORLDGUARD_PLUGIN.wrapPlayer((org.bukkit.entity.Player) player.getDefault()), Flags.PVP) != StateFlag.State.DENY;
	}

	private static ApplicableRegionSet getRegionSet(Location location) {
		RegionManager regionManager = getRegionManager(location.getWorld());
		if (regionManager == null)
			return null;
		BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
		return regionManager.getApplicableRegions(vector);
	}
	
	private static RegionManager getRegionManager(World world) {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt((org.bukkit.World) world.getDefault()));
	}
	
	public static boolean isInAreas(Location baseLoc, List<String> list) {
		for(String s : list)
			if(isInArea(baseLoc, s))
				return true;
		return false;
	}
	
	public static boolean isInArea(Location baseLoc, String area) {
		ApplicableRegionSet region = getRegionSet(baseLoc);
		if(region == null)
			return false;
		
		for(ProtectedRegion r : region.getRegions())
			if(r.getId().equalsIgnoreCase(area))
				return true;
		return false;
	}
}
