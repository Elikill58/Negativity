package com.elikill58.negativity.spigot.integration.worldguard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.Adapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

// TODO RedNesto: split this class in two (for WG 6 & WG 7) both in separate Gradle projects
public class WorldGuardSupport {
	
	private static final WorldGuardPlugin WORLDGUARD_PLUGIN = (WorldGuardPlugin) Adapter.getAdapter().getPlugin("WorldGuard").getDefault();
	private static StateFlag PVP_FLAG;
	private static Constructor<?> createVectorCons = null;
	private static Method createVectorMethod = null, regionContainerGetMethod = null,
				worldAdaptMethod = null;
	private static Method applicaticableRegionMethod = null;
	private static Object worldGuard = null, regionContainer = null;
	
	static {
		try {
			Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
			worldGuard = worldGuardClass.getMethod("getInstance").invoke(null);
		} catch (Exception e) {/*We ignore because it's an old version*/}
		try {
			if (worldGuard != null) {
				Object platform = worldGuard.getClass().getMethod("getPlatform").invoke(worldGuard);
				regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);
				worldAdaptMethod = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter").getMethod("adapt", org.bukkit.World.class);
				regionContainerGetMethod = regionContainer.getClass().getMethod("get", com.sk89q.worldedit.world.World.class);
				PVP_FLAG = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.Flags").getField("PVP").get(null);
			} else {
				regionContainer = WORLDGUARD_PLUGIN.getRegionContainer();
				regionContainerGetMethod = regionContainer.getClass().getMethod("get", org.bukkit.World.class);
				PVP_FLAG = (StateFlag) Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag").getField("PVP").get(null);
			}
			try {
				Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
				createVectorCons = vectorClass.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
				applicaticableRegionMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
			} catch (ClassNotFoundException ex) {
				Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
				createVectorMethod = vectorClass.getMethod("at", Double.TYPE, Double.TYPE, Double.TYPE);
				applicaticableRegionMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
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

		return checkSet.queryState(WORLDGUARD_PLUGIN.wrapPlayer((org.bukkit.entity.Player) player.getDefault()), PVP_FLAG) != StateFlag.State.DENY;
	}

	private static ApplicableRegionSet getRegionSet(Location location) {
		RegionManager regionManager = getRegionManager(location.getWorld());
		if (regionManager == null)
			return null;
		try {
			Object vector = createVectorMethod == null
					? createVectorCons.newInstance(location.getX(), location.getY(), location.getZ())
					: createVectorMethod.invoke(null, location.getX(), location.getY(),
							location.getZ());
			return (ApplicableRegionSet) applicaticableRegionMethod.invoke(regionManager, vector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static RegionManager getRegionManager(World world) {
		RegionManager regionManager = null;
		try {
			regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer,
					worldAdaptMethod == null ? world.getDefault() : worldAdaptMethod.invoke(null, world.getDefault()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return regionManager;
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
