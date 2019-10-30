package com.elikill58.negativity.universal.ban;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.ban.support.BanPluginSupport;

public class Ban {

	public static File banDir;
	public static boolean banActive;
	public static BanType banType = BanType.UNKNOW;
	public static final HashMap<String, String> DB_CONTENT = new HashMap<>();
	public static List<BanPluginSupport> BAN_SUPPORT = new ArrayList<>();

	public static boolean isBanned(NegativityAccount np) {
		if (!banActive)
			return false;
		try {
			np.loadBanRequest(true);
			if (np.getBanRequest().size() == 0)
				return false;
			boolean isBanned = false;
			long time = System.currentTimeMillis();
			for (BanRequest br : np.getBanRequest())
				if (((br.getFullTime()) > time || br.isDef()) && !br.isUnban())
					isBanned = true;
			return isBanned;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static int i;

	public static void manageBan(Cheat cheat, NegativityPlayer np, int relia) {
		Adapter ada = Adapter.getAdapter();
		if (!cheat.isActive() || !ada.getBooleanInConfig("ban.active"))
			return;
		if (!(ada.getIntegerInConfig("ban.reliability_need") <= relia
				&& ada.getIntegerInConfig("ban.alert_need") <= np.getAllWarn(cheat)))
			return;
		String tempCmd = ada.getStringInConfig("ban.other_plugin.command_to_run");
		if (!tempCmd.equalsIgnoreCase("")) {
			ada.runConsoleCommand(tempCmd.replaceAll("%uuid%", np.getUUID().toString()).replaceAll("%ip%", np.getIP())
					.replaceAll("%name%", np.getName()).replaceAll("%reason%", np.getReason(cheat))
					.replaceAll("%alert%", "" + np.getWarn(cheat))
					.replaceAll("%all_alert%", "" + np.getAllWarn(cheat)));
		} else {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			i = -1;
			try {
				i = Integer.parseInt(engine.eval(
						ada.getStringInConfig("ban.time.calculator").replaceAll("%reliability%", String.valueOf(relia))
								.replaceAll("%alert%", String.valueOf(np.getWarn(cheat))
									.replaceAll("%all_alert%", "" + np.getAllWarn(cheat))))
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (BAN_SUPPORT.size() > 0) {
				new BanRequest(np.getAccount(), "Cheat (" + np.getReason(cheat) + ")", i,
						np.getAccount().getBanRequest().size() >= ada.getIntegerInConfig("ban.def.ban_time"),
						BanType.PLUGIN, np.getReason(cheat), "Negativity", false).execute();
			} else
				new BanRequest(np.getAccount(), "Cheat (" + np.getReason(cheat) + ")", i,
						np.getAccount().getBanRequest().size() >= ada.getIntegerInConfig("ban.def.ban_time"),
						banType, np.getReason(cheat), "Negativity", false).execute();
		}
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		banDir = new File(adapter.getDataFolder(), adapter.getStringInConfig("ban.file.dir"));
		if (!(banActive = adapter.getBooleanInConfig("ban.active")))
			return;
		String storage = adapter.getStringInConfig("ban.type");
		if (storage == null) {
			adapter.log(
					"Some line is missing in the configuration file. Please, remove it then restart your server to get all configuration line.");
			return;
		}
		if (storage.equalsIgnoreCase("file")) {
			banType = BanType.FILE;
		} else if (storage.equalsIgnoreCase("db") || storage.equalsIgnoreCase("database")) {
			banType = BanType.DATABASE;
		} else if (storage.equalsIgnoreCase("command") || storage.equalsIgnoreCase("cmd")) {
			banType = BanType.COMMAND;
		} else if (storage.equalsIgnoreCase("other") || storage.equalsIgnoreCase("other_pl") || storage.equalsIgnoreCase("pl")) {
			banType = BanType.PLUGIN;
		} else {
			adapter.error("Error while loading ban system. " + storage + " is an undefined storage type.");
			adapter.error("Please, write a good storage type in the configuration, then restart you server.");
			banActive = false;
			return;
		}
		if (banType.equals(BanType.FILE))
			if (!banDir.exists())
				banDir.mkdirs();
		DB_CONTENT.putAll(adapter.getKeysListInConfig("ban.db.other"));
	}

	public static boolean canConnect(NegativityAccount np) {
		if (!banActive)
			return true;
		for (BanRequest br : np.getBanRequest())
			if ((br.isDef() || (br.getFullTime()) > System.currentTimeMillis()) && !br.isUnban())
				return false;
		return true;
	}

	public static void addBanPlugin(BanPluginSupport bp) {
		BAN_SUPPORT.add(bp);
	}
}
