package com.elikill58.negativity.spigot.support;

import org.bukkit.entity.Player;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.player.PlayerManager;

public class GadgetMenuSupport {

	public static boolean checkGadgetsMenuPreconditions(Player p) {
		PlayerManager pm = GadgetsMenuAPI.getPlayerManager(p);
		return pm != null && (pm.isFallDamageDisabled() || pm.isFireDamageDisabled() || pm.isBlockDamageDisabled());
	}
	
}
