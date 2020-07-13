package com.elikill58.negativity.bungee;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.BasePlatformPermissionChecker;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePermissionChecker extends BasePlatformPermissionChecker {

	@Override
	protected boolean doPlatformCheck(NegativityPlayer player, String platformPerm) {
		return ((ProxiedPlayer) player.getPlayer()).hasPermission(platformPerm);
	}
}
