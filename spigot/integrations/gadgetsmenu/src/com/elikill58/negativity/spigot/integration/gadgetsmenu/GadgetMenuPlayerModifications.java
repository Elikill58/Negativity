package com.elikill58.negativity.spigot.integration.gadgetsmenu;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.playerModifications.PlayerModifications;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsProvider;
import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.player.PlayerManager;

public class GadgetMenuPlayerModifications implements PlayerModifications {

	@Override
	public boolean shouldIgnoreMovementChecks(Player player) {
		PlayerManager pm = GadgetsMenuAPI.getPlayerManager((org.bukkit.entity.Player) player.getDefault());
		return pm.isFallDamageDisabled() || pm.isFireDamageDisabled() || pm.isBlockDamageDisabled();
	}
	
	public static class Provider implements PlayerModificationsProvider, PluginDependentExtension {
		
		@Override
		public PlayerModifications create(Adapter adapter) {
			return new GadgetMenuPlayerModifications();
		}
		
		@Override
		public String getPluginId() {
			return "GadgetsMenu";
		}
	}
}
