package com.elikill58.negativity.spigot.support;

import java.util.UUID;

import org.geysermc.floodgate.FloodgateAPI;

public class FloodGateSupport {

	public static boolean isBedrockPlayer(UUID uuid) {
		return FloodgateAPI.isBedrockPlayer(uuid);
	}
}
