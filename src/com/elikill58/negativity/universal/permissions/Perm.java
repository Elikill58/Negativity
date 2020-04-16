package com.elikill58.negativity.universal.permissions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public class Perm {

	public static final String PLATFORM_CHECKER = "platform";

	private static String checkerId = PLATFORM_CHECKER;
	private static final Map<String, PermissionChecker> checkers = new HashMap<>();

	public static boolean hasPerm(NegativityPlayer np, String perm) {
		PermissionChecker checker = getActiveChecker();
		return checker != null && checker.hasPermission(np, perm);
	}

	@Nullable
	public static PermissionChecker getActiveChecker() {
		return checkers.get(checkerId);
	}

	public static void registerChecker(String checkerId, PermissionChecker checker) {
		checkers.put(checkerId, checker);
	}

	public static void init() {
		Adapter store = Adapter.getAdapter();
		checkerId = store.getStringInConfig("Permissions.checker");
	}
}
