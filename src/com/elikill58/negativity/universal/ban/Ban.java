package com.elikill58.negativity.universal.ban;

import java.io.File;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;

public class Ban {

	public static File banDir;
	public static boolean banFileActive, banDbActive, banActive;
	public static final HashMap<String, String> DB_CONTENT = new HashMap<>();

	public static boolean isBanned(NegativityAccount np) {
		if(!banActive)
			return false;
		try {
			np.loadBanRequest();
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

	public static void manageBan(Cheat cheat, NegativityPlayer np, int relia) {
		Adapter ada = Adapter.getAdapter();
		if (!cheat.isActive() || !ada.getBooleanInConfig("ban.active"))
			return;
		if (!(ada.getIntegerInConfig("ban.reliability_need") <= relia
				&& ada.getIntegerInConfig("ban.alert_need") <= np.getWarn(cheat)))
			return;
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		int i = -1;
		try {
			i = Integer.parseInt(engine.eval(
					ada.getStringInConfig("ban.time.calculator").replaceAll("%reliability%", String.valueOf(relia))
							.replaceAll("%alert%", String.valueOf(np.getWarn(cheat))))
					.toString());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		new BanRequest(np, "Cheat (" + cheat.getName() + ")", i,
				np.getBanRequest().size() >= ada.getIntegerInConfig("ban.def.ban_time"), BanType.PLUGIN,
				cheat.getName(), "Negativity", false).execute();
	}

	public static void init() {
		Adapter adapter = Adapter.getAdapter();
		banDir = new File(adapter.getDataFolder(), adapter.getStringInConfig("ban.file.dir"));
		banDbActive = adapter.getBooleanInConfig("ban.db.isActive");
		banActive = adapter.getBooleanInConfig("ban.active");
		banFileActive = !banDbActive;
		if (banFileActive)
			if (!banDir.exists())
				banDir.mkdirs();
		DB_CONTENT.putAll(adapter.getKeysListInConfig("ban.db.other"));
	}

	public static boolean canConnect(NegativityAccount np) {
		if(!banActive)
			return true;
		for (BanRequest br : np.getBanRequest())
			if ((br.isDef() || (br.getFullTime()) > System.currentTimeMillis()) && !br.isUnban())
				return false;
		return true;
	}
}
