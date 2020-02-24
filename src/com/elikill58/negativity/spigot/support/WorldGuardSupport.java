package com.elikill58.negativity.spigot.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardSupport {
	
	private static final WorldGuardPlugin worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
	private static StateFlag PVP_FLAG;
	private static Constructor<?> vectorConstructor = null;
	private static Method vectorConstructorAsAMethodBecauseWhyNot = null, regionContainerGetMethod = null,
				worldAdaptMethod = null, regionManagerGetMethod = null;
	private static Object worldGuard = null, regionContainer = null;
	
	static {
		try {
			Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
			Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
			worldGuard = getInstanceMethod.invoke(null);
		} catch (Exception e) {/*We ignore because it's an old version*/}
		try {
			if (worldGuard != null) {
				Object platform = worldGuard.getClass().getMethod("getPlatform").invoke(worldGuard);
				regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);
				worldAdaptMethod = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter").getMethod("adapt", World.class);
				regionContainerGetMethod = regionContainer.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World"));
				PVP_FLAG = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.Flags").getField("PVP").get(null);
			} else {
				regionContainer = worldGuardPlugin.getRegionContainer();
				regionContainerGetMethod = regionContainer.getClass().getMethod("get", World.class);
				PVP_FLAG = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag").getField("PVP").get(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInRegionProtected(Player p) {
		return isPVPAllowed(p, p.getLocation());
	}

	public static boolean isPVPAllowed(Player player, Location location) {
		ApplicableRegionSet checkSet = getRegionSet(location);
		if (checkSet == null)
			return true;

		return checkSet.queryState(worldGuardPlugin.wrapPlayer(player), PVP_FLAG) != StateFlag.State.DENY;
	}

	private static ApplicableRegionSet getRegionSet(Location location) {
		RegionManager regionManager = getRegionManager(location.getWorld());
		if (regionManager == null)
			return null;
		try {
			Object vector = vectorConstructorAsAMethodBecauseWhyNot == null
					? vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ())
					: vectorConstructorAsAMethodBecauseWhyNot.invoke(null, location.getX(), location.getY(),
							location.getZ());
			return (ApplicableRegionSet) regionManagerGetMethod.invoke(regionManager, vector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static RegionManager getRegionManager(World world) {
		RegionManager regionManager = null;
		try {
			regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer,
					worldAdaptMethod == null ? world : worldAdaptMethod.invoke(null, world));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return regionManager;
	}
}
