package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
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

	/**
	 * Opens a bundled file as an InputStream, located under {@code assets/negativity/[name]}
	 *
	 * @param name the name of the bundled file
	 *
	 * @return the InputStream of the bundled file, or null if it does not exist
	 *
	 * @throws IOException if an IO exception occurred
	 */
	@Nullable
	public abstract InputStream openBundledFile(String name) throws IOException;

	/**
	 * Copies a {@link #openBundledFile} bundled file} to the file denoted by the given Path
	 *
	 * @param name the name of the bundled file
	 * @param destFile the file Path it will be copied to
	 *
	 * @return the file Path it is copied to, or null if the bundled file does not exist
	 *
	 * @throws IOException if an IO exception occurred
	 */
	@Nullable
	public Path copyBundledFile(String name, Path destFile) throws IOException {
		if (Files.notExists(destFile)) {
			Files.createDirectories(destFile.getParent());
			try (InputStream bundled = openBundledFile(name)) {
				if (bundled == null) {
					return null;
				}
				Files.copy(bundled, destFile);
			}
		}

		return destFile;
	}

	public abstract LoggerAdapter getLogger();
	
	/**
	 * @deprecated Use {@code getLogger().info(msg)} instead
	 */
	@Deprecated
	public abstract void log(String msg);
	
	/**
	 * @deprecated Use {@code getLogger().warn(msg)} instead
	 */
	@Deprecated
	public abstract void warn(String msg);
	
	/**
	 * @deprecated Use {@code getLogger().error(msg)} instead
	 */
	@Deprecated
	public abstract void error(String msg);
	public abstract void debug(String msg);
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
	@Deprecated
	public abstract void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof);
	public abstract void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, CheatHover hover);
	public abstract void runConsoleCommand(String cmd);
	public abstract CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId);
	public abstract List<UUID> getOnlinePlayers();
}
