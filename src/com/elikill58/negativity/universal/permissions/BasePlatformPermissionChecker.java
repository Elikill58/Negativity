package com.elikill58.negativity.universal.permissions;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class BasePlatformPermissionChecker implements PermissionChecker {

	@Override
	public final boolean hasPermission(NegativityPlayer player, String permission) {
		String platformPerm = Adapter.getAdapter().getConfig().getString("Permissions." + permission + ".default");
		if (platformPerm == null) {
			return false;
		}

		return doPlatformCheck(player, platformPerm);
	}

	protected abstract boolean doPlatformCheck(NegativityPlayer player, String platformPerm);
}
