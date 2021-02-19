package com.elikill58.negativity.spigot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.elikill58.negativity.spigot.support.WorldGuardSupport;
import com.elikill58.negativity.universal.Cheat;

public class WorldRegionBypass {

	private static final List<WorldRegionBypass> REGIONS_BYPASS = new ArrayList<>();
	private static final boolean IS_ENABLED;
	
	static {
		ConfigurationSection section = SpigotNegativity.getInstance().getConfig().getConfigurationSection("region-bypass");
		if(section != null) {
			IS_ENABLED = section.getBoolean("enabled", false);
			for(String keys : section.getKeys(false)) {
				if(keys.equalsIgnoreCase("enabled"))
					continue;
				new WorldRegionBypass(section.getConfigurationSection(keys));
			}
		} else
			IS_ENABLED = false;
	}
	
	public static boolean hasBypass(Cheat c, Location loc) {
		if(loc.getWorld() == null || !loc.getWorld().isChunkLoaded(loc.getBlockX(), loc.getBlockZ()))
			return false;
		if(!IS_ENABLED || REGIONS_BYPASS.isEmpty())
			return false;
		List<WorldRegionBypass> list = REGIONS_BYPASS.stream().distinct().filter((by) -> by.getCheatKeys().contains(c.getKey())).collect(Collectors.toList());
		if(list.isEmpty())
			return false;
		for(WorldRegionBypass bypass : list) {
			if(bypass.getWorlds().contains(loc.getWorld().getName().toLowerCase()))
				return true;
			if(SpigotNegativity.worldGuardSupport && WorldGuardSupport.isInAreas(loc, bypass.getRegions()))
				return true;
		}
		return false;
	}
	
	private final List<String> cheats = new ArrayList<>();
	private final List<String> regions, worlds;
	
	public WorldRegionBypass(ConfigurationSection section) {
		for(String possibleCheats : section.getStringList("cheats")) {
			if(possibleCheats.equalsIgnoreCase("all")) {
				cheats.addAll(Cheat.CHEATS_BY_KEY.keySet());
			} else {
				Cheat cheat = Cheat.fromString(possibleCheats);
				if(cheat == null)
					SpigotNegativity.getInstance().getLogger().info("Cannot find the cheat " + possibleCheats + " in region bypass (Path: " + section.getCurrentPath() + ")");
				else
					cheats.add(cheat.getKey());
			}
		}
		
		regions = section.getStringList("regions");
		worlds = section.getStringList("worlds").stream().map((s) -> s.toLowerCase()).collect(Collectors.toList());
		
		REGIONS_BYPASS.add(this);
	}
	
	public List<String> getCheatKeys() {
		return cheats;
	}

	public List<String> getRegions() {
		return regions;
	}

	public List<String> getWorlds() {
		return worlds;
	}
}
