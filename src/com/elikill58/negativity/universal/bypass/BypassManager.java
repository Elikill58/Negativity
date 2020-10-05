package com.elikill58.negativity.universal.bypass;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.checkers.AliveBypass;
import com.elikill58.negativity.universal.bypass.checkers.ItemUseBypass;
import com.elikill58.negativity.universal.bypass.checkers.WorldRegionBypass;

public class BypassManager {
	
	private static final List<BypassChecker> BYPASS_CHECKER = new ArrayList<>();

	/**
	 * Load all bypass.
	 * 
	 * Warn: it clean all previous Bypass checker.
	 */
	public static void loadBypass() {
		BYPASS_CHECKER.clear();
		
		addBypassChecker(new AliveBypass());
		
		Adapter ada = Adapter.getAdapter();
		Configuration configItems = ada.getConfig().getSection("items");
		if(configItems != null) {
			configItems.getKeys().forEach((key) -> {
				addBypassChecker(new ItemUseBypass(key, configItems.getString(key + ".cheats"), configItems.getString(key + ".when")));
			});
		}

		Configuration sectionRegionBypass = ada.getConfig().getSection("region-bypass");//.getConfigurationSection("region-bypass");
		if(sectionRegionBypass != null) {
			if(sectionRegionBypass.getBoolean("enabled")) {
				for(String keys : sectionRegionBypass.getKeys()) {
					if(keys.equalsIgnoreCase("enabled"))
						continue;
					addBypassChecker(new WorldRegionBypass(sectionRegionBypass.getSection(keys)));
				}
			}
		}
	}
	
	/**
	 * Add a bypass checker
	 * 
	 * @param bc the bypass checker to add
	 */
	public static void addBypassChecker(BypassChecker bc) {
		BYPASS_CHECKER.add(bc);
	}
	
	/**
	 * Check if the player have at least one bypass.
	 * 
	 * @param p the player which we are looking to bypass
	 * @param c the cheat that we are checking
	 * @return true if the player can bypass
	 */
	public static boolean hasBypass(Player p, Cheat c) {
		for(BypassChecker bc : BYPASS_CHECKER)
			if(bc.hasBypass(p, c))
				return true;
		return false;
	}
}
