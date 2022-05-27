package com.elikill58.negativity.spigot.integration.worldguard.v7;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.playerModifications.PlayerModifications;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsProvider;

public class WorldGuardPlayerModifications implements PlayerModifications {
	
	@Override
	public boolean isProtected(Player player, Entity damager) {
		return WorldGuardV7Support.isInRegionProtected(player);
	}
	
	public static class Provider implements PlayerModificationsProvider, PluginDependentExtension {
		
		@Override
		public PlayerModifications create(Adapter adapter) {
			return new WorldGuardPlayerModifications();
		}
		
		@Override
		public String getPluginId() {
			return "WorldGuard";
		}
	}
}
