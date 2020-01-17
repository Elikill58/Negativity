package com.elikill58.negativity.universal.ban;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.ban.support.BanPluginSupport;

public class Ban {

	public static File banDir;
	public static boolean banActive;
	public static BanType banType = BanType.UNKNOW;
	public static final HashMap<String, String> DB_CONTENT = new HashMap<>();
	public static final List<BanPluginSupport> BAN_SUPPORT = new ArrayList<>();

	public static void init() {
		DB_CONTENT.clear();
		Adapter adapter = Adapter.getAdapter();
		banDir = new File(adapter.getDataFolder(), adapter.getStringInConfig("ban.file.dir"));
		if (!(banActive = adapter.getBooleanInConfig("ban.active")))
			return;
		String storage = adapter.getStringInConfig("ban.type");
		if (storage == null) {
			adapter.log(
					"Some line is missing in the configuration file. Please, remove config.yml then restart your server to get all configuration line.");
			return;
		}
		if (storage.equalsIgnoreCase("file")) {
			banType = BanType.FILE;
			if (!banDir.exists())
				banDir.mkdirs();
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
		DB_CONTENT.putAll(adapter.getKeysListInConfig("ban.db.other"));
	}

	public static void addBanPlugin(BanPluginSupport bp) {
		BAN_SUPPORT.add(bp);
	}
}
