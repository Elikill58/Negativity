package com.elikill58.negativity.universal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
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
	
	public abstract Platform getPlatformID();

	public String getName() {
		return getPlatformID().getName();
	}
	
	public abstract Configuration getConfig();
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

	/**
	 * Get a logger adapter to log informations.
	 * 
	 * @return the logger
	 */
	public abstract LoggerAdapter getLogger();
	
	public abstract void debug(String msg);
	public abstract TranslationProviderFactory getPlatformTranslationProviderFactory();
	public List<Cheat> getAbstractCheats() {
		return Cheat.CHEATS;
	}
	public abstract void reload();
	public abstract String getVersion();
	public abstract String getPluginVersion();
	public abstract void reloadConfig();

	public abstract NegativityAccountManager getAccountManager();
	public abstract void runConsoleCommand(String cmd);
	public abstract CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId);
	public abstract List<UUID> getOnlinePlayersUUID();
	public abstract List<Player> getOnlinePlayers();
	public abstract double[] getTPS();
	public abstract double getLastTPS();
	
	public abstract ItemRegistrar getItemRegistrar();
	public abstract ItemBuilder createItemBuilder(Material type);
	public abstract ItemBuilder createSkullItemBuilder(Player owner);

	public abstract Location createLocation(World w, double x, double y, double z);
	public abstract Inventory createInventory(String inventoryName, int size, NegativityHolder holder);
	public abstract OfflinePlayer getOfflinePlayer(String name);
	public abstract OfflinePlayer getOfflinePlayer(UUID uuid);
	public abstract Player getPlayer(String name);
	public abstract Player getPlayer(UUID uuid);
	public abstract FakePlayer createFakePlayer(Location loc, String name);
	
	/**
	 * Send message to the specified player which can run a command and have a message showed when the mouse is drag hover the message.
	 * 
	 * @param p the player that will receive the message
	 * @param message the message
	 * @param hover the hover message (show on mouse drag)
	 * @param command the command run on click
	 */
	public abstract void sendMessageRunnableHover(Player p, String message, String hover, String command);
	
	// Other plugin management
	/**
	 * Check if another plugin is loaded.
	 * 
	 * @param name the plugin name (used by the platform)
	 * @return true if the plugin is loaded.
	 */
	public abstract boolean hasPlugin(String name);
	
	/**
	 * Get another plugin
	 * 
	 * @param name the plugin name (used by the platform)
	 * @return
	 */
	public abstract ExternalPlugin getPlugin(String name);
	
	/**
	 * Run action sync with the server.
	 * Specially useful for world/player action
	 * 
	 * @param call the action to call
	 */
	public abstract void runSync(Runnable call);
}
