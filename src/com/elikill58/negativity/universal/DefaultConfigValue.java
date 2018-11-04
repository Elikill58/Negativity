package com.elikill58.negativity.universal;

import java.util.HashMap;

import com.elikill58.negativity.universal.adapter.Adapter;

public class DefaultConfigValue {

	public static final HashMap<String, Integer> INTS = new HashMap<>();
	public static final HashMap<String, String> STRINGS = new HashMap<>();
	public static final HashMap<String, Boolean> BOOLEANS = new HashMap<>();
	public static final HashMap<String, Double> DOUBLES = new HashMap<>();

	public static int getDefaultValueInt(String dir) {
		if(INTS.containsKey(dir))
			return INTS.get(dir);
		else {
			Adapter.getAdapter().warn("Unknow default int value: " + dir);
			return -1;
		}
	}

	public static String getDefaultValueString(String dir) {
		if(STRINGS.containsKey(dir))
			return STRINGS.get(dir);
		else {
			Adapter.getAdapter().warn("Unknow default string value: " + dir);
			return dir;
		}
	}

	public static boolean getDefaultValueBoolean(String dir) {
		if(BOOLEANS.containsKey(dir))
			return BOOLEANS.get(dir);
		else {
			Adapter.getAdapter().warn("Unknow default boolean value: " + dir);
			return false;
		}
	}
	
	public static double getDefaultValueDouble(String dir) {
		if(DOUBLES.containsKey(dir))
			return DOUBLES.get(dir);
		else {
			Adapter.getAdapter().warn("Unknow default double value: " + dir);
			return -1;
		}
	}

	public static void init() {
		BOOLEANS.put("log_alerts", true);
		INTS.put("tps_alert_stop", 18);
		BOOLEANS.put("report_command", true);
		BOOLEANS.put("ban_command", true);
		BOOLEANS.put("unban_command", true);
		BOOLEANS.put("Database.isActive", true);
		
		STRINGS.put("Database.url", "127.0.0.1/myDb");
		STRINGS.put("Database.user", "root");
		STRINGS.put("Database.password", "myPassword");
		STRINGS.put("Database.table_perm", "myTable");
		STRINGS.put("Database.table_lang", "myTable");
		STRINGS.put("Database.table_ban", "ban");
		BOOLEANS.put("Database.saveInCache", true);
		
		BOOLEANS.put("inventory.alerts.see.no_started_verif_cheat", false);
		BOOLEANS.put("inventory.alerts.no_started_verif_cheat", false);
		BOOLEANS.put("inventory.alerts.see.only_cheat_active", true);
		BOOLEANS.put("inventory.alerts.only_cheat_active", true);
		BOOLEANS.put("inventory.inv_freeze_active", true);

		BOOLEANS.put("Permissions.defaultActive", true);
		BOOLEANS.put("Permissions.canBeHigher", false);

		STRINGS.put("Permissions.showAlert.default", "negativity.alert");
		STRINGS.put("Permissions.showAlert.custom", "MOD,ADMIN");
		STRINGS.put("Permissions.verif.default", "negativity.verif");
		STRINGS.put("Permissions.verif.custom", "MOD,ADMIN");
		STRINGS.put("Permissions.mod.default", "negativity.mod");
		STRINGS.put("Permissions.mod.custom", "MOD,ADMIN");
		STRINGS.put("Permissions.manageCheat.default", "negativity.managecheat");
		STRINGS.put("Permissions.manageCheat.custom", "MOD,ADMIN");
		STRINGS.put("Permissions.report_wait.default", "negativity.reportwait");
		STRINGS.put("Permissions.report_wait.custom", "negativity.reportwait");
		STRINGS.put("Permissions.notBanned.default", "negativity.notbanned");
		STRINGS.put("Permissions.notBanned.custom", "ADMIN");
		STRINGS.put("Permissions.ban.default", "negativity.ban");
		STRINGS.put("Permissions.ban.custom", "MOD,ADMIN");
		BOOLEANS.put("Permissions.bypass.active", false);
		
		String[] cheats = new String[] {"all", "forcefield", "fastplace", "speedhack", "autoclick", "fly", "antipotion", "autoeat", "autoregen", "antiknockback", "jesus", "nofall", "blink", "spider", "fastbow", "scaffold", "step", "noslowdown", "fastladders", "phase", "autosteal", "edited_client"};
		for(String localCheat : cheats) {
			//STRINGS.put("Permissions.bypass." + localCheat, "negativity.bypass." + localCheat);
			STRINGS.put("Permissions.bypass." + localCheat + ".default", "negativity.bypass." + localCheat);
			STRINGS.put("Permissions.bypass." + localCheat + ".custom", "ADMIN");
		}

		STRINGS.put("Translation.no_active_file_name", "messages.yml");
		BOOLEANS.put("Translation.active", false);
		STRINGS.put("Translation.lang_available", "en_US");
		STRINGS.put("Translation.lang_available", "fr_FR");
		STRINGS.put("Translation.lang_available", "no_NO");
		STRINGS.put("Translation.lang_available", "pt_BR");
		BOOLEANS.put("Translation.use_db", true);
		STRINGS.put("Translation.default", "en_US");

		BOOLEANS.put("hasBungeecord", false);
		
		INTS.put("time_between_report", 1000);

		BOOLEANS.put("ban.active", true);
		BOOLEANS.put("ban.destroy_when_unban", false);
		INTS.put("ban.reliability_need", 90);
		INTS.put("ban.alert_need", 5);
		STRINGS.put("ban.time.calculator", "360000000 + (%reliability% * 10 * %alert%)");
		INTS.put("ban.def.ban_time", 4);
		BOOLEANS.put("ban.file.isActive", false);
		STRINGS.put("ban.file.dir", "ban");
		BOOLEANS.put("ban.db.isActive", false);
		STRINGS.put("ban.db.column.uuid", "uuid");
		STRINGS.put("ban.db.column.time", "time");
		STRINGS.put("ban.db.column.def", "def");
		STRINGS.put("ban.db.column.reason", "reason");
		STRINGS.put("ban.db.column.other.name", "%name%");
		
		for(String lc : cheats) {
			INTS.put("cheats." + lc + ".ping", 150);
			STRINGS.put("cheats." + lc + ".exact_name", lc);
			BOOLEANS.put("cheats." + lc + ".isActive", true);
			INTS.put("cheats." + lc + ".reliability_alert", 60);
			BOOLEANS.put("cheats." + lc + ".autoVerif", true);
			BOOLEANS.put("cheats." + lc + ".setBack", false);
			BOOLEANS.put("cheats." + lc + ".kick", false);
			INTS.put("cheats." + lc + ".alert_kick", 5);
		}
		
		DOUBLES.put("cheats.forcefield", 3.9);
		INTS.put("cheats.autoclick.click_alert", 20);
	}
}
