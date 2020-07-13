package com.elikill58.negativity.velocity;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.BasePlatformPermissionChecker;
import com.velocitypowered.api.proxy.Player;

public class VelocityPermissionChecker extends BasePlatformPermissionChecker {

	@Override
	protected boolean doPlatformCheck(NegativityPlayer player, String platformPerm) {
		return ((Player) player.getPlayer()).hasPermission(platformPerm);
	}
}
