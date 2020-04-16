package com.elikill58.negativity.sponge;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.PermissionChecker;
import com.elikill58.negativity.universal.permissions.Perms;

public class SpongePermissionChecker implements PermissionChecker {

	@Override
	public boolean hasPermission(NegativityPlayer player, String permission) {
		String platformPerm = Perms.PLATFORM_PERMS.get(permission);
		if (platformPerm == null) {
			return false;
		}

		return ((Player) player.getPlayer()).hasPermission(platformPerm);
	}
}
