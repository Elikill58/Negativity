package com.elikill58.negativity.sponge7;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.ChannelRegistrationException;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

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
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge7.impl.entity.SpongeFakePlayer;
import com.elikill58.negativity.sponge7.impl.entity.SpongeOfflinePlayer;
import com.elikill58.negativity.sponge7.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemBuilder;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemRegistrar;
import com.elikill58.negativity.sponge7.impl.plugin.SpongeExternalPlugin;
import com.elikill58.negativity.sponge7.nms.SpongeVersionAdapter;
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

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeAdapter extends Adapter {

	private final LoggerAdapter logger;
	private final SpongeNegativity plugin;
	private Configuration config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpongeNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final SpongeItemRegistrar itemRegistrar;
	private final Version serverVersion;
	private final Scheduler scheduler;
	
	public SpongeAdapter(SpongeNegativity sn) {
		this.plugin = sn;
		this.logger = new Slf4jLoggerAdapter(sn.getLogger());
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(sn.getDataFolder().resolve("lang"), "Negativity", "CheatHover");
		this.itemRegistrar = new SpongeItemRegistrar();
		this.serverVersion = Version.getVersionByName(getVersion());
		this.scheduler = new SpongeScheduler(sn);
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.SPONGE;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder().toFile();
	}

	@Override
	public void debug(String msg) {
		if(getConfig().getBoolean("debug", false))
			logger.info(msg);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		plugin.reloadCommands();
	}

	@Override
	public String getVersion() {
		return Sponge.getPlatform().getMinecraftVersion().getName();
	}
	
	@Override
	public Version getServerVersion() {
		return this.serverVersion;
	}
	
	@Override
	public String getPluginVersion() {
		return plugin.getContainer().getVersion().orElse("unknown");
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
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				ConfigurationNode rootNode = GsonConfigurationLoader.builder()
						.setSource(() -> new BufferedReader(new StringReader(response)))
						.build()
						.load();
				return rootNode.getNode("isMcleaks").getBoolean(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		for (org.spongepowered.api.entity.living.player.Player temp : Sponge.getServer().getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public double[] getTPS() {
		return new double[] {getLastTPS()};
	}

	@Override
	public double getLastTPS() {
		return Sponge.getServer().getTicksPerSecond();
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		LiteralText text = Text.builder(message)
			.onHover(TextActions.showText(Text.of(hover)))
			.onClick(TextActions.runCommand(command))
			.build();
		((org.spongepowered.api.entity.living.player.Player) p.getDefault()).sendMessage(text);
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (org.spongepowered.api.entity.living.player.Player temp : Sponge.getServer().getOnlinePlayers())
			list.add(SpongeEntityManager.getPlayer(temp));
		return list;
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
		return new SpongeItemBuilder(owner);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		return new SpongeItemBuilder(owner);
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpongeInventory(inventoryName, size, holder);
	}

	@Override
	public @Nullable Player getPlayer(String name) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(name).orElse(null));
	}

	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(uuid).orElse(null));
	}

	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		Player online = getPlayer(name);
		if (online != null) {
			return online;
		}
		return Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
			.get(name).map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		Player online = getPlayer(uuid);
		if (online != null) {
			return online;
		}
		return Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
			.get(uuid).map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		return new SpongeFakePlayer(loc, name);
	}

	@Override
	public boolean hasPlugin(String name) {
		return Sponge.getPluginManager().isLoaded(name);
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpongeExternalPlugin(Sponge.getPluginManager().getPlugin(name).orElse(null));
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return Sponge.getPluginManager().getPlugins().stream()
				.filter(plugin -> plugin.getDependency("negativity").isPresent())
				.map(SpongeExternalPlugin::new)
				.collect(Collectors.toList());
	}
	
	@Override
	public void runSync(Runnable call) {
		Task.builder().execute(call).submit(plugin);
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public boolean canSendStats() {
		return Sponge.getMetricsConfigManager().areMetricsEnabled(plugin.getContainer());
	}
	
	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		RawDataChannel spongeChannel = null;
		try {
			spongeChannel = Sponge.getChannelRegistrar().getOrCreateRaw(plugin, channel);
		} catch (ChannelRegistrationException e) {
			try {
				Class<?> vanillaRawChannelClass = Class.forName("org.spongepowered.server.network.VanillaRawDataChannel");
				Constructor<?> rawChannelConstructor = vanillaRawChannelClass.getConstructor(ChannelRegistrar.class, String.class, PluginContainer.class);
				spongeChannel = (RawDataChannel) rawChannelConstructor.newInstance(Sponge.getChannelRegistrar(), channel, plugin.getContainer()); // new channel instance
		        // now register channel
				Class<?> vanillaChannelRegistrarClass = Class.forName("org.spongepowered.server.network.VanillaChannelRegistrar");
				Method registerChannel = vanillaChannelRegistrarClass.getDeclaredMethod("registerChannel", Class.forName("org.spongepowered.server.network.VanillaChannelBinding"));
				registerChannel.setAccessible(true);
				registerChannel.invoke(Sponge.getChannelRegistrar(), spongeChannel);
			} catch (Exception exc) {
				exc.printStackTrace();
				plugin.getLogger().warn("Failed to register channel " + channel + " even with second method: " + exc.getMessage());
			}
		}
		if(spongeChannel == null)
			return;
		spongeChannel.addListener((data, connection, side) -> {
			if(side == Type.CLIENT) {
				Sponge.getServer().getOnlinePlayers().forEach((p) -> {
					if(p.getConnection().getAddress().equals(connection.getAddress())) {
						event.accept(SpongeEntityManager.getPlayer(p), data.array());
					}
				});
			}
		});;
	}
	
	@Override
	public void broadcastMessage(String message) {
		Sponge.getServer().getBroadcastChannel().send(Text.of(message));
	}
	
	@Override
	public VersionAdapter<?> getVersionAdapter() {
		return SpongeVersionAdapter.getVersionAdapter();
	}
	
	@Override
	public List<String> getAllPlugins() {
		return Sponge.getPluginManager().getPlugins().stream().map(PluginContainer::getId).collect(Collectors.toList());
	}
}
