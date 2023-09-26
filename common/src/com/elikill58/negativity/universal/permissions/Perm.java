package com.elikill58.negativity.universal.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;

public class Perm {

	public static final String ADMIN = "admin";
	public static final String CHECK = "check";
	public static final String CHAT_CLEAR = "chat.clear";
	public static final String CHAT_LOCK = "chat.lock";
	public static final String CHAT_LOCK_BYPASS = "chat.lock_bypass";
	public static final String SHOW_ALERT = "showAlert";
	public static final String SHOW_REPORT = "showReport";
	public static final String VERIF = "verif";
	public static final String MANAGE_CHEAT = "manageCheat";
	public static final String RELOAD = "reload";
	public static final String REPORT_WAIT = "report_wait";
	public static final String REPORT = "report";
	public static final String BAN = "ban";
	public static final String UNBAN = "unban";
	public static final String MOD = "mod";
	public static final String LANG = "lang";
	public static final String WARN = "warn";
	public static final String BYPASS_ALL = "bypass.all";
	public static final String BYPASS_BAN = "bypass.ban";

	public static final String PLATFORM_CHECKER = "platform";

	private static String checkerId = PLATFORM_CHECKER;
	private static final List<String> perms = new ArrayList<>(Arrays.asList(ADMIN, CHECK, CHAT_CLEAR, CHAT_LOCK, CHAT_LOCK_BYPASS, SHOW_ALERT, SHOW_REPORT, VERIF, MANAGE_CHEAT, RELOAD, REPORT, REPORT_WAIT, BAN, UNBAN, MOD, LANG, WARN, BYPASS_ALL, BYPASS_BAN));
	private static final Map<String, PermissionChecker> checkers = new HashMap<>();
	private static final Map<String, String> permissions = new HashMap<>();

	public static boolean hasPerm(CommandSender sender, String perm) {
		if(sender instanceof Player) {
			PermissionChecker checker = getActiveChecker();
			return checker != null && checker.hasPermission(NegativityPlayer.getNegativityPlayer((Player) sender), permissions.getOrDefault(perm, perm));
		}
		return true;
	}

	public static boolean hasPerm(NegativityPlayer np, String perm) {
		PermissionChecker checker = getActiveChecker();
		return checker != null && checker.hasPermission(np, permissions.get(perm));
	}

	@Nullable
	public static PermissionChecker getActiveChecker() {
		return checkers.get(checkerId);
	}

	public static void registerChecker(String checkerId, PermissionChecker checker) {
		checkers.put(checkerId, checker);
	}

	public static void init() {
		checkerId = Adapter.getAdapter().getConfig().getString("Permissions.checker");
		
		registerChecker(PLATFORM_CHECKER, new DefaultPermissionChecker());
		
		for(String all : values())
			permissions.put(all, Adapter.getAdapter().getConfig().getString("Permissions." + all + ".default", "negativity." + all));
	}
	
	public static List<String> values() {
		return perms;
	}
	
	public static Map<String, String> getPermissions() {
		return permissions;
	}
}
