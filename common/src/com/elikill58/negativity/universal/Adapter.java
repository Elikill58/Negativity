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
	
	/**
	 * Get the platform
	 * 
	 * @return the platform
	 */
	public abstract Platform getPlatformID();

	/**
	 * Get the platform name
	 * 
	 * @return the platform name
	 */
	public String getName() {
		return getPlatformID().getName();
	}
	
	/**
	 * Get the Negativity's config of the platform
	 * 
	 * @return the Negativity configuration
	 */
	public abstract Configuration getConfig();
	
	/**
	 * Get the data folder of the plugin
	 * 
	 * @return the data folder
	 */
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
	
	/**
	 * Reload Negativity's plugin
	 */
	public abstract void reload();
	
	/**
	 * Get the platform version
	 * 
	 * @return the platform version
	 */
	public abstract String getVersion();
	
	/**
	 * Get the version of the Negativity plugin
	 * 
	 * @return the version of Negativity
	 */
	public abstract String getPluginVersion();
	
	/**
	 * Reload the configuration of Negativity
	 */
	public abstract void reloadConfig();

	/**
	 * Get the account manager of the platform
	 * 
	 * @return the account manager
	 */
	public abstract NegativityAccountManager getAccountManager();
	
	/**
	 * Run a command from console
	 * 
	 * @param cmd the command which have to be execute
	 */
	public abstract void runConsoleCommand(String cmd);
	
	/**
	 * Check if the UUID is a McLeaks account
	 * 
	 * @param playerId the player to check
	 * @return a completable boolean
	 */
	public abstract CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId);
	
	/**
	 * Get UUID of all online players
	 * 
	 * @return online players UUID
	 */
	public abstract List<UUID> getOnlinePlayersUUID();
	
	/**
	 * Get all online players
	 * 
	 * @return all online players
	 */
	public abstract List<Player> getOnlinePlayers();
	
	/**
	 * Get all available TPS
	 * This array is not empty, at least, it contains the {@link #getLastTPS()} value in [0] place
	 * 
	 * @return tps
	 */
	public abstract double[] getTPS();
	
	/**
	 * Get last TPS value
	 * 
	 * @return last TPS
	 */
	public abstract double getLastTPS();
	
	/**
	 * Get the registrar of all items
	 * 
	 * @return the item registrar
	 */
	public abstract ItemRegistrar getItemRegistrar();
	
	/**
	 * Create a new item builder with the given type
	 * 
	 * @param type the item type
	 * @return a new item builder of itemstack with the given type
	 */
	public abstract ItemBuilder createItemBuilder(Material type);
	
	/**
	 * Create a new item builder with the given type name
	 * 
	 * @param type the item type name
	 * @return a new item builder of itemstack with the given type name
	 */
	public abstract ItemBuilder createItemBuilder(String type);
	
	/**
	 * Create a new item builder of skull with the current owner
	 * 
	 * @param owner the player owner of the skull
	 * @return an item builder of the skull
	 */
	public abstract ItemBuilder createSkullItemBuilder(Player owner);

	/**
	 * Create a new location
	 * 
	 * @param w the world location
	 * @param x the X location
	 * @param y the Y location
	 * @param z the Z location
	 * @return a new location
	 */
	public abstract Location createLocation(World w, double x, double y, double z);
	
	/**
	 * Create a new inventory
	 * 
	 * @param inventoryName the inventory name
	 * @param size the inventory size
	 * @param holder the inventory holder
	 * @return a new inventory
	 */
	public abstract Inventory createInventory(String inventoryName, int size, NegativityHolder holder);
	
	/**
	 * Get offline player with the given name
	 * Prefer use {@link #getOfflinePlayer(UUID)} because this method isn't supported on all platform, and a name can be changed
	 * 
	 * @param name the offline player name
	 * @return the offline player
	 */
	public abstract OfflinePlayer getOfflinePlayer(String name);
	
	/**
	 * Get offline player with the given UUID
	 * 
	 * @param uuid the offline player UUID
	 * @return the offline player
	 */
	public abstract OfflinePlayer getOfflinePlayer(UUID uuid);

	/**
	 * Get player with the given name
	 * Prefer use {@link #getPlayer(UUID)} because this method isn't supported on all platform, and a name can be changed
	 * 
	 * @param name the player name
	 * @return the player
	 */
	public abstract Player getPlayer(String name);
	
	/**
	 * Get player with the given UUID
	 * 
	 * @param uuid the player UUID
	 * @return the player
	 */
	public abstract Player getPlayer(UUID uuid);
	
	/**
	 * Create a fake player
	 * WARN: Not supported yet with all platform
	 * 
	 * @param loc the location of the fake player
	 * @param name the name of the fake player
	 * @return the fake player
	 */
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
	 * @return all plugins depending on Negativity
	 */
	public abstract List<ExternalPlugin> getDependentPlugins();
	
	/**
	 * Run action sync with the server.
	 * Specially useful for world/player action
	 * 
	 * @param call the action to call
	 */
	public abstract void runSync(Runnable call);
}
