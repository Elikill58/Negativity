package com.elikill58.negativity.spigot.support.floodgate;

import java.util.UUID;

import org.geysermc.floodgate.api.FloodgateApi;

public class FloodGateV2Support {

	public static boolean isBedrockPlayer(UUID uuid) {
		return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
	}
}
