package com.elikill58.negativity.spigot;

import org.bukkit.entity.Player;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.BasePlatformPermissionChecker;

public class BukkitPermissionChecker extends BasePlatformPermissionChecker {

	@Override
	protected boolean doPlatformCheck(NegativityPlayer player, String platformPerm) {
		return ((Player) player.getPlayer()).hasPermission(platformPerm);
	}
}
