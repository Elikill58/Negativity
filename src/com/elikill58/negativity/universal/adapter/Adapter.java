package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;

public abstract class Adapter {

	private static Adapter adapter = null;

	public static void setAdapter(Adapter adapter) {
		if(Adapter.adapter != null) {
			try {
				throw new IllegalAccessException("No ! You don't must to change the Adapter !");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		Adapter.adapter = adapter;
		adapter.loadLang();
	}

	public static Adapter getAdapter() {
		return adapter;
	}

	public abstract String getName();
	public abstract Object getConfig();
	public abstract File getDataFolder();
	public abstract String getStringInConfig(String dir);
	public abstract boolean getBooleanInConfig(String dir);
	public abstract int getIntegerInConfig(String dir);
	public abstract double getDoubleInConfig(String dir);
	public abstract List<String> getStringListInConfig(String dir);
	public abstract HashMap<String, String> getKeysListInConfig(String dir);
	public abstract String getStringInOtherConfig(String fileDir, String valueDir, String fileName);
	public abstract File copy(String lang, File f);
	public abstract void log(String msg);
	public abstract void warn(String msg);
	public abstract void error(String msg);
	public abstract void set(String dir, Object value);
	public abstract void loadLang();
	public abstract String getStringFromLang(String lang, String key);
	public abstract List<String> getStringListFromLang(String lang, String key);
	public List<Cheat> getAbstractCheats() {
		return Cheat.CHEATS;
	}
	public abstract void reload();
	public abstract Object getItem(String itemName);
	public abstract String getVersion();
	public abstract void reloadConfig();
	@Nonnull
	public abstract NegativityAccount getNegativityAccount(UUID playerId);
	@Nullable
	public abstract NegativityPlayer getNegativityPlayer(UUID playerId);
	public abstract void invalidateAccount(UUID playerId);
}
