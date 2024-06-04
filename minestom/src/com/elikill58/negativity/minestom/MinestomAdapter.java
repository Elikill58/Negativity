package com.elikill58.negativity.minestom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.entity.MinestomOfflinePlayer;
import com.elikill58.negativity.minestom.impl.inventory.MinestomInventory;
import com.elikill58.negativity.minestom.impl.item.MinestomItemBuilder;
import com.elikill58.negativity.minestom.impl.item.MinestomItemRegistrar;
import com.elikill58.negativity.minestom.impl.location.MinestomWorld;
import com.elikill58.negativity.minestom.impl.plugin.MinestomExternalPlugin;
import com.elikill58.negativity.minestom.nms.MinestomVersionAdapter;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.hollowcube.minestom.extensions.ExtensionBootstrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.DiscoveredExtension;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

public class MinestomAdapter extends Adapter {

	private final LoggerAdapter logger;
	private final MinestomNegativity plugin;
	private Configuration config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(null);
	private final TranslationProviderFactory translationProviderFactory;
	private final MinestomItemRegistrar itemRegistrar;
	private final Version serverVersion;
	private final Scheduler scheduler;
	
	public MinestomAdapter(MinestomNegativity sn, Logger logger) {
		this.plugin = sn;
		this.logger = new Slf4jLoggerAdapter(logger);
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(sn.getDataDirectory().resolve("lang"), "Negativity", "CheatHover");
		this.itemRegistrar = new MinestomItemRegistrar();
		this.serverVersion = Version.getVersionByProtocolID(MinecraftServer.PROTOCOL_VERSION);
		this.scheduler = new MinestomScheduler();
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.MINESTOM;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataDirectory().toFile();
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
	}

	@Override
	public String getVersion() {
		return MinecraftServer.VERSION_NAME;
	}
	
	@Override
	public Version getServerVersion() {
		return this.serverVersion;
	}
	
	@Override
	public String getPluginVersion() {
		return MinestomNegativity.getInstance().getOrigin().getVersion();
	}

	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		MinecraftServer.getCommandManager().executeServerCommand(cmd);
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		return MinestomNegativity.getOnlinePlayers().stream().map(net.minestom.server.entity.Player::getUuid).collect(Collectors.toList());
	}

	@Override
	public double[] getTPS() {
		return new double[] { 20 };
	}

	@Override
	public double getLastTPS() {
		return getTPS()[0];
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		((net.minestom.server.entity.Player) p.getDefault()).sendMessage(Component.text(message).hoverEvent(HoverEvent.showText(Component.text(hover))).clickEvent(ClickEvent.runCommand(command)));
	}

	@Override
	public List<Player> getOnlinePlayers() {
		return MinestomNegativity.getOnlinePlayers().stream().map(MinestomEntityManager::getPlayer).collect(Collectors.toList());
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new MinestomItemBuilder(type);
	}

	@Override
	public ItemBuilder createItemBuilder(ItemStack item) {
		return new MinestomItemBuilder(item);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new MinestomItemBuilder(itemRegistrar.get(type.split(":")[0]));
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new MinestomItemBuilder(owner);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		return new MinestomItemBuilder(owner);
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new MinestomInventory(inventoryName, size, holder);
	}

	@Override
	public @Nullable Player getPlayer(String name) {
		return MinestomEntityManager.getPlayer(MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(name));
	}

	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return MinestomEntityManager.getPlayer(MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid));
	}

	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		Player p = getPlayer(name);
		return p == null ? new MinestomOfflinePlayer(null, name) : p;
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		Player p = getPlayer(uuid);
		return p == null ? new MinestomOfflinePlayer(uuid, null) : p;
	}

	@Override
	public boolean hasPlugin(String name) {
		return ExtensionBootstrap.getExtensionManager().hasExtension(name);
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		Extension e = ExtensionBootstrap.getExtensionManager().getExtension(name);
		return e == null ? null : new MinestomExternalPlugin(e);
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public void runSync(Runnable call) {
		MinecraftServer.getSchedulerManager().submitTask(() -> {
			call.run();
			return TaskSchedule.stop();
		});
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public boolean canSendStats() {
		return false;
	}
	
	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		
	}
	
	@Override
	public void broadcastMessage(String message) {
		getOnlinePlayers().forEach(p -> p.sendMessage(message));
	}
	
	@Override
	public VersionAdapter<?> getVersionAdapter() {
		return MinestomVersionAdapter.getVersionAdapter();
	}
	
	@Override
	public List<String> getAllPlugins() {
		return ExtensionBootstrap.getExtensionManager().getExtensions().stream().map(Extension::getOrigin).map(DiscoveredExtension::getName).collect(Collectors.toList());
	}

	@Override
	public World getServerWorld(Player p) {
		Instance i = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(p.getUniqueId()).getInstance();
		return World.getWorld(i.getUniqueId().toString(), (a) -> new MinestomWorld(i));
	}
}
