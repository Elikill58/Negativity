package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotOfflinePlayer;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemBuilder;
import com.elikill58.negativity.spigot.impl.item.SpigotItemRegistrar;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.plugin.SpigotExternalPlugin;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.JavaLoggerAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotAdapter extends Adapter {

	private JavaPlugin pl;
	private final Configuration config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpigotNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private final SpigotItemRegistrar itemRegistrar;

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(
				pl.getDataFolder().toPath().resolve("lang"), "Negativity", "CheatHover");
		this.logger = new JavaLoggerAdapter(pl.getLogger());
		this.itemRegistrar = new SpigotItemRegistrar();
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
		if (UniversalUtils.DEBUG)
			pl.getLogger().info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) {
		return pl.getResource("assets/negativity/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		Negativity.loadNegativity();
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
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
		List<UUID> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
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
			try {
				Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer");
				Object server = mcServer.getMethod("getServer").invoke(mcServer);
				return (double[]) server.getClass().getField("recentTps").get(server);
			} catch (Exception e) {
				getLogger().warn("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
				e.printStackTrace();
				return new double[] {20, 20, 20};
			}
		}
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return new SpigotLocation(w, x, y, z);
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
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpigotItemBuilder(owner);
	}

	@Override
	public Player getPlayer(String name) {
		org.bukkit.entity.Player p = Bukkit.getPlayer(name);
		if(p == null)
			return null;
		return new SpigotPlayer(p);
	}

	@Override
	public Player getPlayer(UUID uuid) {
		org.bukkit.entity.Player p = Bukkit.getPlayer(uuid);
		if(p == null)
			return null;
		return new SpigotPlayer(p);
	}

	@SuppressWarnings("deprecation")
	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(name);
		if(p == null)
			return null;
		return new SpigotOfflinePlayer(p);
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
		if(p == null)
			return null;
		return new SpigotOfflinePlayer(p);
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
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
}
