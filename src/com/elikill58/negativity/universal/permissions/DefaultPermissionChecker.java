package com.elikill58.negativity.universal.permissions;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;

public class DefaultPermissionChecker implements PermissionChecker {

	@Override
	public boolean hasPermission(NegativityPlayer player, String permission) {
		return player.getPlayer().hasPermission(Adapter.getAdapter().getConfig().getString("Permissions." + permission + ".default", permission));
	}

}
