package com.elikill58.negativity.sponge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

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
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;

public class SpongeAdapter extends Adapter {
	
	@Override
	public Platform getPlatformID() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Configuration getConfig() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public File getDataFolder() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Nullable
	@Override
	public InputStream openBundledFile(String name) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public LoggerAdapter getLogger() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void debug(String msg) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void reload() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public String getVersion() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public String getPluginVersion() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void reloadConfig() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public NegativityAccountManager getAccountManager() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void runConsoleCommand(String cmd) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public List<UUID> getOnlinePlayersUUID() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public double[] getTPS() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public double getLastTPS() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public ItemRegistrar getItemRegistrar() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public ItemBuilder createItemBuilder(Material type) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Location createLocation(World w, double x, double y, double z) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Player getPlayer(String name) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Player getPlayer(UUID uuid) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public boolean hasPlugin(String name) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public void runSync(Runnable call) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
