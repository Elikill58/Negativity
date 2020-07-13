package com.elikill58.negativity.universal.permissions;

import com.elikill58.negativity.common.NegativityPlayer;

/**
 * PermissionCheckers are used by Negativity to determine if a player has the right to do something.
 * Checkers responses are based on their own criteria.
 */
public interface PermissionChecker {

	boolean hasPermission(NegativityPlayer player, String permission);
}
