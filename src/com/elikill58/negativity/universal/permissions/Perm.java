package com.elikill58.negativity.universal.permissions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public class Perm {

	public static final String SHOW_ALERT = "showAlert";
	public static final String SHOW_REPORT = "showReport";
	public static final String VERIF = "verif";
	public static final String MANAGE_CHEAT = "manageCheat";
	public static final String REPORT_WAIT = "report_wait";
	public static final String REPORT = "report";
	public static final String BAN = "ban";
	public static final String UNBAN = "unban";
	public static final String NOT_BANNED = "notBanned";
	public static final String MOD = "mod";
	public static final String LANG = "lang";

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
