package com.elikill58.negativity.spigot.support;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Version;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardSupport {
	
	public static boolean isInRegionProtected(Player p) {
		try {
			switch(Version.getVersion()) {
			case HIGHER:
				break;
			case V1_14:
			case V1_13:
				Location loc = p.getLocation();
				RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(p.getWorld()));
				for(ProtectedRegion pr : regionManager.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).getRegions())
					if((State) pr.getFlag(Flags.PVP) == State.ALLOW)
						return true;
				break;
			case V1_12:
			case V1_11:
			case V1_10:
			case V1_9:
			case V1_8:
			case V1_7:
				Class<?> wgBukkitClass = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
				Object regionManagerObj = wgBukkitClass.getMethod("getRegionManager", p.getWorld().getClass()).invoke(wgBukkitClass, p.getWorld());
				Object applicableRegionObj = regionManagerObj.getClass().getMethod("getApplicableRegions", p.getLocation().getClass()).invoke(regionManagerObj, p.getLocation());
				Class<?> defaultFlag = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
				return (boolean) applicableRegionObj.getClass().getMethod("allows", defaultFlag).invoke(applicableRegionObj, defaultFlag.getField("PVP").get(defaultFlag));
			default:
				SpigotNegativity.getInstance().getLogger().info("Cannot load an available version of WorldGuard.");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
