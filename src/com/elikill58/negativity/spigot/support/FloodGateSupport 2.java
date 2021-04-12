package com.elikill58.negativity.spigot.support;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.FloodgateAPI;

public class FloodGateSupport {

	public static boolean isBedrockPlayer(Player p) {
		return FloodgateAPI.isBedrockPlayer(p.getUniqueId());
	}
}
