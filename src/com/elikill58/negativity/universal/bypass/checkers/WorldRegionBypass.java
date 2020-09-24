package com.elikill58.negativity.universal.bypass.checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.bypass.BypassChecker;
import com.elikill58.negativity.universal.support.WorldGuardSupport;

public class WorldRegionBypass implements BypassChecker {

	public boolean hasBypass(Player p, Cheat c) {
		Location loc = p.getLocation();
		if(getWorlds().contains(loc.getWorld().getName().toLowerCase()))
			return true;
		if(Negativity.worldGuardSupport && WorldGuardSupport.isInAreas(loc, getRegions()))
			return true;
		return false;
	}
	
	private final List<Cheat> cheats = new ArrayList<>();
	private final List<String> regions, worlds;
	
	public WorldRegionBypass(Configuration section) {
		for(String possibleCheats : section.getStringList("cheats")) {
			Cheat cheat = Cheat.fromString(possibleCheats);
			if(cheat == null)
				Adapter.getAdapter().getLogger().info("Cannot find the cheat " + possibleCheats + " in region bypass");
			else
				cheats.add(cheat);
		}
		
		regions = section.getStringList("regions");
		worlds = section.getStringList("worlds").stream().map((s) -> s.toLowerCase()).collect(Collectors.toList());
	}
	
	public List<Cheat> getCheats() {
		return cheats;
	}

	public List<String> getRegions() {
		return regions;
	}

	public List<String> getWorlds() {
		return worlds;
	}
}
