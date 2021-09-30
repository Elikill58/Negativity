package com.elikill58.negativity.spigot.integration.protocolsupport;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionFetcher;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionFetcherProvider;

import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportPlayerVersionFetcher implements PlayerVersionFetcher {

	@Override
	public Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(ProtocolSupportAPI.getProtocolVersion((org.bukkit.entity.Player) p.getDefault()).getId());
	}
	
	@Override
	public Integer getPlayerProtocolVersion(Player p) {
		return ProtocolSupportAPI.getProtocolVersion((org.bukkit.entity.Player) p.getDefault()).getId();
	}
	
	public static class Provider implements PlayerVersionFetcherProvider, PluginDependentExtension {
		
		@Override
		public PlayerVersionFetcher create(Adapter adapter) {
			return new ProtocolSupportPlayerVersionFetcher();
		}
		
		@Override
		public String getPluginId() {
			return "ProtocolSupport";
		}
	}
}
