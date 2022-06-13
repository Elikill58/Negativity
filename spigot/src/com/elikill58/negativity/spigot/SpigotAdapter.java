package com.elikill58.negativity.spigot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotOfflinePlayer;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemBuilder;
import com.elikill58.negativity.spigot.impl.item.SpigotItemRegistrar;
import com.elikill58.negativity.spigot.impl.plugin.SpigotExternalPlugin;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.JavaLoggerAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotAdapter extends Adapter {

	private final JavaPlugin pl;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpigotNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private final SpigotItemRegistrar itemRegistrar;
	private final Version serverVersion;
	private final Scheduler scheduler;
	private Configuration config;

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(
				pl.getDataFolder().toPath().resolve("lang"), "Negativity", "CheatHover");
		this.logger = new JavaLoggerAdapter(pl.getLogger());
		this.itemRegistrar = new SpigotItemRegistrar();
		this.serverVersion = Version.getVersion(getVersion());
		this.scheduler = new SpigotScheduler(pl);
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.SPIGOT;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void debug(String msg) {
		if (getConfig().getBoolean("debug", false))
			pl.getLogger().info("[Debug] " + msg);
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
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
	@Override
	public Version getServerVersion() {
		return this.serverVersion;
	}
	
	@Override
	public String getPluginVersion() {
		return pl.getDescription().getVersion();
	}

	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Bukkit.getScheduler().callSyncMethod(pl, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Object data = new JSONParser().parse(response);
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					Object isMcleaks = json.get("isMcleaks");
					if (isMcleaks != null) {
						return Boolean.getBoolean(isMcleaks.toString());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		return Utils.getOnlinePlayers().stream().map(org.bukkit.entity.Player::getUniqueId).collect(Collectors.toList());
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(NegativityPlayer.getNegativityPlayer(temp.getUniqueId(), () -> new SpigotPlayer(temp)).getPlayer());
		return list;
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}

	@Override
	public double[] getTPS() {
		if(SpigotNegativity.isCraftBukkit) {
			return new double[] {20, 20, 20};
		} else {
			return SpigotVersionAdapter.getVersionAdapter().getTps();
		}
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpigotInventory(inventoryName, size, holder);
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		new ClickableText().addRunnableHoverEvent(message, hover, command).sendToPlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new SpigotItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createItemBuilder(ItemStack item) {
		return new SpigotItemBuilder(item);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new SpigotItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpigotItemBuilder(owner);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		return new SpigotItemBuilder(owner);
	}

	@Override
	public @Nullable Player getPlayer(String name) {
		return SpigotEntityManager.getPlayer(Bukkit.getPlayer(name));
	}

	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return SpigotEntityManager.getPlayer(Bukkit.getPlayer(uuid));
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		Player online = getPlayer(name);
		if (online != null) {
			return online;
		}
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(name);
		if (p.hasPlayedBefore()) {
			return new SpigotOfflinePlayer(p);
		}
		return null;
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		Player online = getPlayer(uuid);
		if (online != null) {
			return online;
		}
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
		if (p.hasPlayedBefore()) {
			return new SpigotOfflinePlayer(p);
		}
		return null;
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		// TODO implement fake player on adapter
		return null;
	}
	
	@Override
	public boolean hasPlugin(String name) {
		return Bukkit.getPluginManager().getPlugin(name) != null;
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpigotExternalPlugin(Bukkit.getPluginManager().getPlugin(name));
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return Arrays.stream(Bukkit.getPluginManager().getPlugins())
				.filter(plugin -> {
					PluginDescriptionFile description = plugin.getDescription();
					return description.getDepend().contains("Negativity") || description.getSoftDepend().contains("Negativity");
				})
				.map(SpigotExternalPlugin::new)
				.collect(Collectors.toList());
	}
	
	@Override
	public void runSync(Runnable call) {
		Bukkit.getScheduler().runTask(pl, call);
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		pl.getServer().getMessenger().registerIncomingPluginChannel(pl, channel, (String ch, org.bukkit.entity.Player p, byte[] data) -> event.accept(SpigotEntityManager.getPlayer(p), data));
	}
	
	@Override
	public void broadcastMessage(String message) {
		Bukkit.broadcastMessage(message);
	}
	
	@Override
	public VersionAdapter<?> getVersionAdapter() {
		return SpigotVersionAdapter.getVersionAdapter();
	}
	
	@Override
	public List<String> getAllPlugins() {
		return Arrays.asList(pl.getServer().getPluginManager().getPlugins()).stream().map(Plugin::getName).collect(Collectors.toList());
	}
}
