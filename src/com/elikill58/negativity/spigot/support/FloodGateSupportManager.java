package com.elikill58.negativity.spigot.support;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.spigot.support.floodgate.FloodGateV1Support;
import com.elikill58.negativity.spigot.support.floodgate.FloodGateV2Support;

public class FloodGateSupportManager {

	public static final List<String> disabledCheat = new ArrayList<>();
	public static boolean hasSupport = false;
	public static boolean isV2 = false;
	
	public static boolean isBedrockPlayer(UUID uuid) {
		if(hasSupport) {
			return isV2 ? FloodGateV2Support.isBedrockPlayer(uuid) : FloodGateV1Support.isBedrockPlayer(uuid);
		}
		return false;
	}
}
