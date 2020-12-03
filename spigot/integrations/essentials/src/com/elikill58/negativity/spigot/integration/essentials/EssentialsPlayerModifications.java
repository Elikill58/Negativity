package com.elikill58.negativity.spigot.integration.essentials;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.playerModifications.PlayerModifications;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsProvider;

public class EssentialsPlayerModifications implements PlayerModifications {

	@Override
	public boolean canFly(Player player) {
		return player.hasPermission("essentials.fly");
	}
	
	@Override
	public boolean isSpeedUnlocked(Player player) {
		return player.hasPermission("essentials.speed");
	}
	
    public static class Provider implements PlayerModificationsProvider, PluginDependentExtension {
	
		@Override
		public PlayerModifications create(Adapter adapter) {
			return new EssentialsPlayerModifications();
		}
	
		@Override
		public String getPluginId() {
			return "Essentials";
		}
	}
}
