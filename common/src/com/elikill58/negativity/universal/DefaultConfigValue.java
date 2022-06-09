package com.elikill58.negativity.universal;

import java.util.HashMap;

import com.elikill58.negativity.universal.permissions.Perm;

public class DefaultConfigValue {

	private static final HashMap<String, Integer> INTS = new HashMap<>();
	private static final HashMap<String, String> STRINGS = new HashMap<>();
	private static final HashMap<String, Boolean> BOOLEANS = new HashMap<>();
	private static final HashMap<String, Double> DOUBLES = new HashMap<>();

	public static int getDefaultValueInt(String dir) {
		if(INTS.containsKey(dir))
			return INTS.get(dir);
		else {
			Adapter.getAdapter().getLogger().warn("Unknown default int value: " + dir);
			return -1;
		}
	}

	public static String getDefaultValueString(String dir) {
		if(STRINGS.containsKey(dir))
			return STRINGS.get(dir);
		else {
			Adapter.getAdapter().getLogger().warn("Unknown default string value: " + dir);
			return dir;
		}
	}

	public static boolean getDefaultValueBoolean(String dir) {
		if(BOOLEANS.containsKey(dir))
			return BOOLEANS.get(dir);
		else {
			Adapter.getAdapter().getLogger().warn("Unknown default boolean value: " + dir);
			return false;
		}
	}

	public static double getDefaultValueDouble(String dir) {
		if(DOUBLES.containsKey(dir))
			return DOUBLES.get(dir);
		else {
			Adapter.getAdapter().getLogger().warn("Unknown default double value: " + dir);
			return -1;
		}
	}

	/**
	 * Load all default values
	 */
	public static void init() {
		BOOLEANS.clear();
		INTS.clear();
		DOUBLES.clear();
		STRINGS.clear();

		BOOLEANS.put("log_alerts", true);
		BOOLEANS.put("log_alerts_in_console", true);
		DOUBLES.put("tps_alert_stop", 19.0);
		INTS.put("time_between_alert", 2000);
		BOOLEANS.put("commands.report", true);
		BOOLEANS.put("commands.kick", true);
		BOOLEANS.put("commands.mod", true);
		BOOLEANS.put("commands.lang", true);

		BOOLEANS.put("Database.isActive", false);
		STRINGS.put("Database.url", "127.0.0.1/myDb");
		STRINGS.put("Database.user", "root");
		STRINGS.put("Database.password", "myPassword");
		STRINGS.put("Database.type", "mysql");

		BOOLEANS.put("inventory.alerts.no_started_verif_cheat", false);
		BOOLEANS.put("inventory.alerts.only_cheat_active", true);
		BOOLEANS.put("inventory.inv_freeze_active", true);

		STRINGS.put("items.COMPASS.when", "always");
		STRINGS.put("items.COMPASS.cheats", "speed,fly");

		STRINGS.put("Permissions.checker", Perm.PLATFORM_CHECKER);

		STRINGS.put("Permissions.admin.default", "negativity.admin");
		STRINGS.put("Permissions.showAlert.default", "negativity.alert");
		STRINGS.put("Permissions.showReport.default", "negativity.seereport");
		STRINGS.put("Permissions.verif.default", "negativity.verif");
		STRINGS.put("Permissions.manageCheat.default", "negativity.managecheat");
		STRINGS.put("Permissions.report_wait.default", "negativity.reportwait");
		STRINGS.put("Permissions.report.default", "negativity.report");
		STRINGS.put("Permissions.ban.default", "negativity.ban");
		STRINGS.put("Permissions.unban.default", "negativity.unban");
		STRINGS.put("Permissions.notBanned.default", "negativity.notbanned");
		STRINGS.put("Permissions.mod.default", "negativity.mod");
		STRINGS.put("Permissions.lang.default", "negativity.lang");
		BOOLEANS.put("Permissions.bypass.active", false);

		String[] cheats = new String[]{"airjump", "antipotion", "fasteat", "regen", "antiknockback", "autoclick", "autosteal",
				"blink", "chat", "critical", "forcefield", "fastplace", "fastladder", "fastbow", "faststairs", "groundspoof", "inventorymove", "jesus", "fly",
				"nofall", "nopitchlimit", "noslowdown", "noweb", "nuker", "phase", "scaffold", "sneak", "speed", "spider", "step", "timer", "xray"};
		for (String localCheat : cheats) {
			//STRINGS.put("Permissions.bypass." + localCheat, "negativity.bypass." + localCheat);
			STRINGS.put("Permissions.bypass." + localCheat + ".default", "negativity.bypass." + localCheat);
		}

		BOOLEANS.put("Translation.active", false);
		STRINGS.put("Translation.default", "en_US");
		STRINGS.put("Translation.provider", TranslatedMessages.PLATFORM_PROVIDER_ID);

		BOOLEANS.put("disableProxyIntegration", false);

		INTS.put("time_between_report", 1000);
		INTS.put("time_between_alert", 1000);

		STRINGS.put("accounts.storage.id", "default");

		INTS.put("verif.time", 2);
		BOOLEANS.put("verif.disable_alert_on_verif", false);
		STRINGS.put("verif.storage.id", "default");

		for(String lc : cheats) {
			INTS.put("cheats." + lc + ".ping", 150);
			STRINGS.put("cheats." + lc + ".exact_name", lc);
			BOOLEANS.put("cheats." + lc + ".isActive", true);
			INTS.put("cheats." + lc + ".reliability_alert", 60);
			BOOLEANS.put("cheats." + lc + ".autoVerif", true);
			BOOLEANS.put("cheats." + lc + ".setBack", false);
			BOOLEANS.put("cheats." + lc + ".kick", false);
			INTS.put("cheats." + lc + ".alert_to_kick", 5);
			BOOLEANS.put("cheats." + lc + ".check_in_verif", true);
		}

		INTS.put("cheats.autoclick.click_alert", 20);

		DOUBLES.put("cheats.forcefield.reach", 3.9);
		BOOLEANS.put("cheats.forcefield.ghost_disabled", false);

		BOOLEANS.put("cheats.nofall.kill", false);
		INTS.put("cheats.nofall.kill-reliability", 90);

		BOOLEANS.put("cheats.fasteat.autoVerif", false);
		INTS.put("cheats.fastplace.time_2_place", 50);

		BOOLEANS.put("cheats.fastbow.autoVerif", false);

		INTS.put("cheats.xray.ping", 300);

		INTS.put("cheats.chat.ping", 300);
		
		INTS.put("cheats.timer.max_variation", 7);

		BOOLEANS.put("cheats.special.mcleaks.kick", true);
		
		STRINGS.put("cheats.special.server_crash.name", "Server Crash");
		BOOLEANS.put("cheats.special.server_crash.kick", true);
		BOOLEANS.put("cheats.special.server_crash.ban", false);
		STRINGS.put("cheats.special.server_crash.ban_time", "2629800000");
		
		STRINGS.put("cheats.special.invalid_name.name", "Invalid Name");
		BOOLEANS.put("cheats.special.invalid_name.kick", true);
		BOOLEANS.put("cheats.special.invalid_name.ban", false);
		STRINGS.put("cheats.special.invalid_name.ban_time", "-1");

		INTS.put("cheats.special.max-player-by-ip.number", 2);
		STRINGS.put("cheats.special.max-player-by-ip.name", "Max Player/IP");
	}
}
