package com.elikill58.negativity.spigot;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.BasePlatformPermissionChecker;

public class BukkitPermissionChecker extends BasePlatformPermissionChecker {

	@Override
	protected boolean doPlatformCheck(NegativityPlayer player, String platformPerm) {
		return player.getPlayer().hasPermission(platformPerm);
	}
}
