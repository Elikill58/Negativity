package com.elikill58.negativity.integration.viaversion;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionFetcher;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionFetcherProvider;
import com.viaversion.viaversion.api.Via;

public class ViaVersionPlayerVersionFetcher implements PlayerVersionFetcher {

	@Override
	public Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(Via.getAPI().getPlayerVersion(p.getUniqueId()));
	}
	
	@Override
	public Integer getPlayerProtocolVersion(Player p) {
		return Via.getAPI().getPlayerVersion(p.getUniqueId());
	}
	
	public static class Provider implements PlayerVersionFetcherProvider, PluginDependentExtension {
		
		@Override
		public PlayerVersionFetcher create(Adapter adapter) {
			return new ViaVersionPlayerVersionFetcher();
		}
		
		@Override
		public String getPluginId() {
			return "ViaVersion";
		}
	}
}
