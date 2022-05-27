package com.elikill58.negativity.spigot.integration.worldguard.v6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.bypass.BypassChecker;
import com.elikill58.negativity.universal.bypass.BypassCheckerProvider;

public class WorldGuardBypassProvider implements BypassCheckerProvider, PluginDependentExtension {
	
	@Override
	public Collection<BypassChecker> create(Adapter adapter) {
		Configuration sectionRegionBypass = adapter.getConfig().getSection("region-bypass");
		if (sectionRegionBypass == null || !sectionRegionBypass.getBoolean("enabled")) {
			return Collections.emptyList();
		}
		
		List<BypassChecker> checkers = new ArrayList<>();
		for (String keys : sectionRegionBypass.getKeys()) {
			if (!keys.equalsIgnoreCase("enabled")) {
				checkers.add(new RegionBypass(sectionRegionBypass.getSection(keys)));
			}
		}
		return checkers;
	}
	
	@Override
	public boolean hasPreRequises() {
		try {
			Class.forName("com.sk89q.worldguard.WorldGuard");
			return false; // this is WG v7 so ignoring
		} catch (Exception e) {
			return true;
		}
	}
	
	@Override
	public String getPluginId() {
		return "WorldGuard";
	}
}
