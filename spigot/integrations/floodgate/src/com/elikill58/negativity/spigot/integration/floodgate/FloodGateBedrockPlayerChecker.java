package com.elikill58.negativity.spigot.integration.floodgate;

import java.util.UUID;

import org.geysermc.floodgate.FloodgateAPI;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerChecker;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerCheckerProvider;

public class FloodGateBedrockPlayerChecker implements BedrockPlayerChecker {
	
	@Override
	public boolean isBedrockPlayer(UUID uuid) {
		return FloodgateAPI.isBedrockPlayer(uuid);
	}
	
	public static class Provider implements BedrockPlayerCheckerProvider, PluginDependentExtension {
		
		@Override
		public BedrockPlayerChecker create(Adapter adapter) {
			return new FloodGateBedrockPlayerChecker();
		}
		
		@Override
		public String getPluginId() {
			return "floodgate-bukkit";
		}
	}
}
