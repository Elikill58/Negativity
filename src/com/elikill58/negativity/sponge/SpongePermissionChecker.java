package com.elikill58.negativity.sponge;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.BasePlatformPermissionChecker;

public class SpongePermissionChecker extends BasePlatformPermissionChecker {

	@Override
	protected boolean doPlatformCheck(NegativityPlayer player, String platformPerm) {
		return player.getPlayer() != null && ((Player) player.getPlayer()).hasPermission(platformPerm);
	}
}
