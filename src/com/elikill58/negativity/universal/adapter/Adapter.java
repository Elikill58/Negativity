package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.events.EventType;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
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
	
	public abstract void debug(String msg);
	public abstract TranslationProviderFactory getPlatformTranslationProviderFactory();
	public List<Cheat> getAbstractCheats() {
		return Cheat.CHEATS;
	}
	public abstract void reload();
	public abstract String getVersion();
	public abstract void reloadConfig();

	public abstract NegativityAccountManager getAccountManager();
	public abstract void alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, CheatHover hover);
	public abstract void runConsoleCommand(String cmd);
	public abstract CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId);
	public abstract List<UUID> getOnlinePlayersUUID();
	public abstract List<Player> getOnlinePlayers();
	public abstract double[] getTPS();
	public abstract double getLastTPS();
	
	public abstract ItemRegistrar getItemRegistrar();
	public abstract ItemBuilder createItemBuilder(Material type);

	public abstract Location createLocation(World w, double x, double y, double z);
	public abstract Inventory createInventory(String inventoryName, int size, NegativityHolder holder);
	public abstract OfflinePlayer getOfflinePlayer(String name);
	public abstract Player getPlayer(String name);
	public abstract Player getPlayer(UUID uuid);
	
	public abstract void sendMessageRunnableHover(Player p, String message, String hover, String command);
	
	public abstract Event callEvent(EventType type, Object... args);
}
