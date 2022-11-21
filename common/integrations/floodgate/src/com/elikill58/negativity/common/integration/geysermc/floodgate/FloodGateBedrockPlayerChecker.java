package com.elikill58.negativity.common.integration.geysermc.floodgate;

import java.util.UUID;

import org.geysermc.floodgate.api.FloodgateApi;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.bedrock.checker.BedrockPlayerChecker;
import com.elikill58.negativity.universal.bedrock.checker.BedrockPlayerCheckerProvider;

public class FloodGateBedrockPlayerChecker implements BedrockPlayerChecker {
	
	@Override
	public boolean isBedrockPlayer(UUID uuid) {
		return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
	}
	
	public static class Provider implements BedrockPlayerCheckerProvider, PluginDependentExtension {
		
		@Override
		public BedrockPlayerChecker create(Adapter adapter) {
			return new FloodGateBedrockPlayerChecker();
		}
		
		@Override
		public String getPluginId() {
			return "floodgate";
		}
	}
}
