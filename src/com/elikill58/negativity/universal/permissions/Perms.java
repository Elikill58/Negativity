package com.elikill58.negativity.universal.permissions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Perms {

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

	public static final Map<String, String> PLATFORM_PERMS;

	static {
		Map<String, String> permsMapping = new HashMap<>();
		permsMapping.put(SHOW_ALERT, "negativity.alert");
		permsMapping.put(SHOW_REPORT, "negativity.seereport");
		permsMapping.put(VERIF, "negativity.verif");
		permsMapping.put(MANAGE_CHEAT, "negativity.managecheat");
		permsMapping.put(REPORT_WAIT, "negativity.reportwait");
		permsMapping.put(REPORT, "negativity.report");
		permsMapping.put(BAN, "negativity.ban");
		permsMapping.put(UNBAN, "negativity.unban");
		permsMapping.put(NOT_BANNED, "negativity.notbanned");
		permsMapping.put(MOD, "negativity.mod");
		permsMapping.put(LANG, "negativity.lang");
		PLATFORM_PERMS = Collections.unmodifiableMap(permsMapping);
	}
}
