package com.elikill58.negativity.testFramework;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

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
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.logger.JavaLoggerAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;

public class TestAdapter extends Adapter {
	
	private final LoggerAdapter logger = new JavaLoggerAdapter(Logger.getLogger("NegativityTest"));
	private Configuration configuration = new Configuration();
	private final File dataFolder = new File("./");
	
	@Override
	public Platform getPlatformID() {
		return Platform.TEST;
	}
	
	@Override
	public Configuration getConfig() {
		return this.configuration;
	}
	
	@Override
	public File getDataFolder() {
		return this.dataFolder;
	}
	
	@Override
	public LoggerAdapter getLogger() {
		return this.logger;
	}
	
	@Override
	public void debug(String msg) {
		this.logger.info(msg);
	}
	
	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return new DummyTranslationProviderFactory();
	}
	
	@Override
	public void reload() {
		Negativity.loadNegativity();
	}
	
	@Override
	public String getVersion() {
		return "test-dev";
	}
	
	@Override
	public Version getServerVersion() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public String getPluginVersion() {
		return "test-dev";
	}
	
	@Override
	public void reloadConfig() {
		this.configuration = new Configuration();
	}
	
	@Override
	public NegativityAccountManager getAccountManager() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public void runConsoleCommand(String cmd) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public List<UUID> getOnlinePlayersUUID() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public double[] getTPS() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public double getLastTPS() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ItemRegistrar getItemRegistrar() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ItemBuilder createItemBuilder(Material type) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public Location createLocation(World w, double x, double y, double z) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public Player getPlayer(String name) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public Player getPlayer(UUID uuid) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public boolean hasPlugin(String name) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public void runSync(Runnable call) {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
	
	@Override
	public Scheduler getScheduler() {
		throw new UnsupportedOperationException("To be implemented"); // TODO
	}
}
