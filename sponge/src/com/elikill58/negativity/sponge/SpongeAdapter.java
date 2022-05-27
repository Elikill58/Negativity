package com.elikill58.negativity.sponge;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.impl.entity.SpongeFakePlayer;
import com.elikill58.negativity.sponge.impl.entity.SpongeOfflinePlayer;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemBuilder;
import com.elikill58.negativity.sponge.impl.item.SpongeItemRegistrar;
import com.elikill58.negativity.sponge.impl.plugin.SpongeExternalPlugin;
import com.elikill58.negativity.sponge.nms.SpongeVersionAdapter;
import com.elikill58.negativity.sponge.utils.Utils;
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SpongeAdapter extends Adapter {
	
	private final SpongeNegativity plugin;
	private final Log4jAdapter logger;
	private Configuration config;
	
	private final Version serverVersion;
	
	private final NegativityTranslationProviderFactory translationProviderFactory;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpongeNegativity::sendPluginMessage);
	private final SpongeItemRegistrar itemRegistrar = new SpongeItemRegistrar();
	private final Scheduler scheduler;
	
	public SpongeAdapter(SpongeNegativity plugin) {
		this.plugin = plugin;
		this.logger = new Log4jAdapter(plugin.getLogger());
		this.config = UniversalUtils.loadConfig(plugin.getConfigDir().resolve("config.yml").toFile(), "config.yml");
		
		this.translationProviderFactory = new NegativityTranslationProviderFactory(plugin.getConfigDir().resolve("lang"), "Negativity", "CheatHover");
		this.serverVersion = Version.getVersionByName(getVersion());
		this.scheduler = new SpongeScheduler(plugin.getContainer());
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.SPONGE8;
	}
	
	@Override
	public Configuration getConfig() {
		return this.config;
	}
	
	@Override
	public File getDataFolder() {
		return this.plugin.getConfigDir().toFile();
	}
	
	@Override
	public LoggerAdapter getLogger() {
		return this.logger;
	}
	
	@Override
	public void debug(String msg) {
		if(getConfig().getBoolean("debug", false))
			this.logger.info(msg);
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
		return Sponge.platform().minecraftVersion().name();
	}
	
	@Override
	public Version getServerVersion() {
		return this.serverVersion;
	}
	
	@Override
	public String getPluginVersion() {
		return this.plugin.getContainer().metadata().version().toString();
	}
	
	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(plugin.getConfigDir().resolve("config.yml").toFile(), "config.yml");
	}
	
	@Override
	public NegativityAccountManager getAccountManager() {
		return this.accountManager;
	}
	
	@Override
	public void runConsoleCommand(String cmd) {
		try {
			Sponge.server().commandManager().process(cmd);
		} catch (CommandException e) {
			this.plugin.getLogger().error("Failed to run command as console", e);
		}
	}
	
	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				ConfigurationNode rootNode = GsonConfigurationLoader.builder()
					.source(() -> new BufferedReader(new StringReader(response)))
					.build()
					.load();
				return rootNode.node("isMcleaks").getBoolean(false);
			} catch (Exception e) {
				this.plugin.getLogger().error("Failed to parse MCLeaks API response", e);
			}
			return false;
		});
	}
	
	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		for (ServerPlayer player : Sponge.server().onlinePlayers()) {
			list.add(player.uniqueId());
		}
		return list;
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (ServerPlayer player : Sponge.server().onlinePlayers()) {
			list.add(SpongeEntityManager.getPlayer(player));
		}
		return list;
	}
	
	@Override
	public double[] getTPS() {
		return new double[]{getLastTPS()};
	}
	
	@Override
	public double getLastTPS() {
		return Sponge.server().ticksPerSecond();
	}
	
	@Override
	public ItemRegistrar getItemRegistrar() {
		return this.itemRegistrar;
	}
	
	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new SpongeItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createItemBuilder(ItemStack item) {
		return new SpongeItemBuilder(item);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new SpongeItemBuilder(itemRegistrar.get(type.split(":")[0]));
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpongeItemBuilder(((org.spongepowered.api.entity.living.player.Player) owner.getDefault()).profile());
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		return new SpongeItemBuilder(((org.spongepowered.api.entity.living.player.User) owner.getDefault()).profile());
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpongeInventory(inventoryName, size, holder);
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		Player online = getPlayer(name);
		if (online != null) {
			return online;
		}
		return Sponge.server().userManager().load(name).join()
			.map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		Player online = getPlayer(uuid);
		if (online != null) {
			return online;
		}
		return Sponge.server().userManager().load(uuid).join()
			.map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public @Nullable Player getPlayer(String name) {
		return SpongeEntityManager.getPlayer(Sponge.server().player(name).orElse(null));
	}
	
	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return SpongeEntityManager.getPlayer(Sponge.server().player(uuid).orElse(null));
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		return new SpongeFakePlayer(loc, name);
	}
	
	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		TextComponent mainText = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
		TextComponent hoverText = LegacyComponentSerializer.legacyAmpersand().deserialize(hover);
		((ServerPlayer) p.getDefault()).sendMessage(mainText.hoverEvent(hoverText).clickEvent(ClickEvent.runCommand(command)));
	}
	
	@Override
	public boolean hasPlugin(String name) {
		return Sponge.pluginManager().plugin(name).isPresent();
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpongeExternalPlugin(Sponge.pluginManager().plugin(name).orElse(null));
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return Sponge.pluginManager().plugins().stream()
			.filter(plugin -> Utils.dependsOn(plugin, "negativity"))
			.map(SpongeExternalPlugin::new)
			.collect(Collectors.toList());
	}
	
	@Override
	public void runSync(Runnable call) {
		Sponge.server().scheduler().submit(Task.builder().plugin(this.plugin.getContainer()).execute(call).build());
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		// TODO this is tricky because we have to register channels in a lifecycle event...
	}

	@Override
	public void broadcastMessage(String message) {
		Sponge.server().broadcastAudience().sendMessage(Component.text(message));
	}
	
	@Override
	public VersionAdapter<?> getVersionAdapter() {
		return SpongeVersionAdapter.getVersionAdapter();
	}
	
	@Override
	public List<String> getAllPlugins() {
		return Sponge.pluginManager().plugins().stream().map(PluginContainer::metadata).map(PluginMetadata::id).collect(Collectors.toList());
	}
}
