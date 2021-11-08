package com.elikill58.negativity.spigot.support.floodgate;

import java.util.UUID;

import org.geysermc.floodgate.FloodgateAPI;

public class FloodGateV1Support {

	public static boolean isBedrockPlayer(UUID uuid) {
		return FloodgateAPI.isBedrockPlayer(uuid);
	}
}
