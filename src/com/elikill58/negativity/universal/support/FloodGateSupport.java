package com.elikill58.negativity.universal.support;

import org.geysermc.floodgate.FloodgateAPI;

import com.elikill58.negativity.api.entity.Player;

public class FloodGateSupport {
	
	public static boolean isBedrockPlayer(Player p) {
		return FloodgateAPI.isBedrockPlayer(p.getUniqueId());
	}
}
