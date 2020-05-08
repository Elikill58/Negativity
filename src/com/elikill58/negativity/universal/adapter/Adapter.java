package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;

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
	}

	public static Adapter getAdapter() {
		return adapter;
	}

	public abstract String getName();
	public abstract ConfigAdapter getConfig();
	public abstract File getDataFolder();

	/**
	 * @deprecated Use {@code getConfig().getString(key)} instead
	 */
	@Deprecated
	public String getStringInConfig(String key) {
		return getConfig().getString(key);
	}

	/**
	 * @deprecated Use {@code getConfig().getBoolean(key)} instead
	 */
	@Deprecated
	public boolean getBooleanInConfig(String key) {
		return getConfig().getBoolean(key);
	}

	/**
	 * @deprecated Use {@code getConfig().getInteger(key)} instead
	 */
	@Deprecated
	public int getIntegerInConfig(String key) {
		return getConfig().getInt(key);
	}

	/**
	 * @deprecated Use {@code getConfig().getDouble(key)} instead
	 */
	@Deprecated
	public double getDoubleInConfig(String key) {
		return getConfig().getDouble(key);
	}

	/**
	 * @deprecated Use {@code getConfig().getStringList(key)} instead
	 */
	@Deprecated
	public List<String> getStringListInConfig(String key) {
		return getConfig().getStringList(key);
	}

	/**
	 * @deprecated Use {@code getConfig().set(key, value)} instead
	 */
	@Deprecated
	public void set(String key, Object value) {
		getConfig().set(key, value);
	}

	public abstract File copy(String lang, File f);
	public abstract void log(String msg);
	public abstract void warn(String msg);
	public abstract void error(String msg);
	public abstract TranslationProviderFactory getPlatformTranslationProviderFactory();
	public List<Cheat> getAbstractCheats() {
		return Cheat.CHEATS;
	}
	public abstract void reload();
	public abstract String getVersion();
	public abstract void reloadConfig();

	/**
	 * @deprecated Use {@link NegativityAccount#get(UUID)} instead
	 */
	@Deprecated
	@Nonnull
	public NegativityAccount getNegativityAccount(UUID playerId) {
		return getAccountManager().getNow(playerId);
	}

	/**
	 * @deprecated Use {@code getAccountManager().dispose(playerId)} instead
	 */
	@Deprecated
	@Nullable
	public NegativityAccount invalidateAccount(UUID playerId) {
		return getAccountManager().dispose(playerId);
	}

	public abstract NegativityAccountManager getAccountManager();
	@Nullable
	public abstract NegativityPlayer getNegativityPlayer(UUID playerId);
	public abstract void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof);
	public abstract void runConsoleCommand(String cmd);
	public abstract CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId);
	public abstract List<UUID> getOnlinePlayers();
}
