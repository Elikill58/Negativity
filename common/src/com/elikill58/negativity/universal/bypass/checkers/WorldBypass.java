package com.elikill58.negativity.universal.bypass.checkers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.BypassChecker;
import com.elikill58.negativity.universal.bypass.BypassCheckerProvider;
import com.elikill58.negativity.universal.keys.CheatKeys;

public class WorldBypass implements BypassChecker {
	
	private final Set<CheatKeys> cheats = new HashSet<>();
	private final List<String> worlds;
	
	public WorldBypass(Configuration section) {
		for(String possibleCheats : section.getStringList("cheats")) {
			if(possibleCheats.equalsIgnoreCase("all")) {
				cheats.addAll(Cheat.getCheatKeys());
			} else {
				Cheat cheat = Cheat.fromString(possibleCheats);
				if(cheat == null)
					Adapter.getAdapter().getLogger().info("Cannot find the cheat " + possibleCheats + " in world bypass");
				else
					cheats.add(cheat.getKey());
			}
		}
		
		worlds = section.getStringList("worlds").stream().map((s) -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
	}

	public List<String> getWorlds() {
		return worlds;
	}

	@Override
	public boolean hasBypass(Player p, Cheat c) {
		Location loc = p.getLocation();
		return getWorlds().contains(loc.getWorld().getName().toLowerCase(Locale.ROOT));
	}
	
	public static class Provider implements BypassCheckerProvider {
		
		@Override
		public Collection<BypassChecker> create(Adapter adapter) {
			Configuration sectionRegionBypass = adapter.getConfig().getSection("region-bypass");
			if (sectionRegionBypass == null || !sectionRegionBypass.getBoolean("enabled")) {
				return Collections.emptyList();
			}
			
			List<BypassChecker> checkers = new ArrayList<>();
			for (String keys : sectionRegionBypass.getKeys()) {
				if (!keys.equalsIgnoreCase("enabled")) {
					checkers.add(new WorldBypass(sectionRegionBypass.getSection(keys)));
				}
			}
			return checkers;
		}
		
	}
}
