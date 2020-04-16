package com.elikill58.negativity.bungee;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.PermissionChecker;
import com.elikill58.negativity.universal.permissions.Perms;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePermissionChecker implements PermissionChecker {

	@Override
	public boolean hasPermission(NegativityPlayer player, String permission) {
		String platformPerm = Perms.PLATFORM_PERMS.get(permission);
		if (platformPerm == null) {
			return false;
		}

		return ((ProxiedPlayer) player.getPlayer()).hasPermission(platformPerm);
	}
}
