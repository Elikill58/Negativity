package com.elikill58.negativity.spigot.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardAPI {

	private static Object worldGuard = null, regionContainer = null;
	private static WorldGuardPlugin worldGuardPlugin = null;
	private static Method regionContainerGetMethod = null, worldAdaptMethod = null, regionManagerGetMethod = null;
	private static Constructor<?> vectorConstructor = null;
	private static Method vectorConstructorAsAMethodBecauseWhyNot = null;
	private static StateFlag pvpFlag;
	private static boolean initialized = false;

	public static boolean isEnabled() {
		return worldGuardPlugin != null;
	}

	public static void init() {
		worldGuardPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		try {
			Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
			Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
			worldGuard = getInstanceMethod.invoke(null);
			SpigotNegativity.getInstance().getLogger().info("Found WorldGuard 7+");
		} catch (Exception ex) {
			SpigotNegativity.getInstance().getLogger().info("Found WorldGuard <7");
		}
	}

	protected static RegionAssociable getAssociable(Player player) {
		return player == null ? Associables.constant(Association.NON_MEMBER) :  worldGuardPlugin.wrapPlayer(player);
	}

	private static void initialize() {
		if (!initialized) {
			initialized = true;
			// Super hacky reflection to deal with differences in WorldGuard 6 and 7+
			try {
				if (worldGuard != null) {
					Object platform = worldGuard.getClass().getMethod("getPlatform").invoke(worldGuard);
					Method getRegionContainerMethod = platform.getClass().getMethod("getRegionContainer");
					regionContainer = getRegionContainerMethod.invoke(platform);
					Class<?> worldEditWorldClass = Class.forName("com.sk89q.worldedit.world.World");
					Class<?> worldEditAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
					worldAdaptMethod = worldEditAdapterClass.getMethod("adapt", World.class);
					regionContainerGetMethod = regionContainer.getClass().getMethod("get", worldEditWorldClass);

					pvpFlag = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.Flags").getField("PVP").get(null);
				} else {
					regionContainer = worldGuardPlugin.getRegionContainer();
					regionContainerGetMethod = regionContainer.getClass().getMethod("get", World.class);
					pvpFlag = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag").getField("PVP").get(null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}

			// Ugh guys, API much?
			try {
				Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
				vectorConstructor = vectorClass.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
				regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
			} catch (Exception ex) {
				try {
					Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
					vectorConstructorAsAMethodBecauseWhyNot = vectorClass.getMethod("at", Double.TYPE, Double.TYPE,
							Double.TYPE);
					regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
				} catch (Exception sodonewiththis) {

				}
			}
		}
	}

	private static RegionManager getRegionManager(World world) {
		initialize();
		if (regionContainer == null || regionContainerGetMethod == null)
			return null;
		RegionManager regionManager = null;
		try {
			if (worldAdaptMethod != null) {
				Object worldEditWorld = worldAdaptMethod.invoke(null, world);
				regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, worldEditWorld);
			} else {
				regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, world);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return regionManager;
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

	public static boolean isPVPAllowed(Player player, Location location) {
		if (worldGuardPlugin == null || location == null)
			return true;
		ApplicableRegionSet checkSet = getRegionSet(location);
		if (checkSet == null)
			return true;

		return checkSet.queryState(getAssociable(player), pvpFlag) != StateFlag.State.DENY;
	}
}
