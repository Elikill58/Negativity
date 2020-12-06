package com.elikill58.negativity.sponge8;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

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
import com.elikill58.negativity.sponge8.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge8.impl.entity.SpongeFakePlayer;
import com.elikill58.negativity.sponge8.impl.entity.SpongeOfflinePlayer;
import com.elikill58.negativity.sponge8.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemBuilder;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemRegistrar;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge8.impl.plugin.SpongeExternalPlugin;
import com.elikill58.negativity.sponge8.utils.Utils;
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
		return Platform.SPONGE;
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
		if (UniversalUtils.DEBUG) {
			this.logger.info(msg);
		}
	}
	
	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}
	
	@Override
	public void reload() {
		reloadConfig();
		SpongeNegativity.trySendProxyPing();
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
		return this.plugin.getContainer().getMetadata().getVersion();
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
			Sponge.getCommandManager().process(cmd);
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
		for (ServerPlayer player : Sponge.getServer().getOnlinePlayers()) {
			list.add(player.getUniqueId());
		}
		return list;
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (ServerPlayer player : Sponge.getServer().getOnlinePlayers()) {
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
		return Sponge.getServer().getTicksPerSecond();
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
	public ItemBuilder createItemBuilder(String type) {
		return new SpongeItemBuilder(itemRegistrar.get(type.split(":")[0]));
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpongeItemBuilder(owner);
	}
	
	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return new SpongeLocation(w, x, y, z);
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpongeInventory(inventoryName, size, holder);
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		return Sponge.getServer().getUserManager().get(name)
			.map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		return Sponge.getServer().getUserManager().get(uuid)
			.map(SpongeOfflinePlayer::new).orElse(null);
	}
	
	@Override
	public Player getPlayer(String name) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(name).orElse(null));
	}
	
	@Override
	public Player getPlayer(UUID uuid) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(uuid).orElse(null));
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
		return Sponge.getPluginManager().isLoaded(name);
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpongeExternalPlugin(Sponge.getPluginManager().getPlugin(name).orElse(null));
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return Sponge.getPluginManager().getPlugins().stream()
			.filter(plugin -> Utils.dependsOn(plugin, "negativity"))
			.map(SpongeExternalPlugin::new)
			.collect(Collectors.toList());
	}
	
	@Override
	public void runSync(Runnable call) {
		Sponge.getServer().getScheduler().submit(Task.builder().plugin(this.plugin.getContainer()).execute(call).build());
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
}
