package com.elikill58.negativity.spigot.integration.worldguard;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.BypassChecker;

public class WorldRegionBypass implements BypassChecker {

	public boolean hasBypass(Player p, Cheat c) {
		Location loc = p.getLocation();
		if(getWorlds().contains(loc.getWorld().getName().toLowerCase(Locale.ROOT)))
			return true;
		return WorldGuardSupport.isInAreas(loc, getRegions());
	}
	
	private final Set<String> cheats = new HashSet<>();
	private final List<String> regions, worlds;
	
	public WorldRegionBypass(Configuration section) {
		for(String possibleCheats : section.getStringList("cheats")) {
			Cheat cheat = Cheat.fromString(possibleCheats);
			if(cheat == null)
				Adapter.getAdapter().getLogger().info("Cannot find the cheat " + possibleCheats + " in region bypass");
			else
				cheats.add(cheat.getKey());
		}
		
		regions = section.getStringList("regions");
		worlds = section.getStringList("worlds").stream().map((s) -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
	}
	
	public List<String> getRegions() {
		return regions;
	}

	public List<String> getWorlds() {
		return worlds;
	}
}
