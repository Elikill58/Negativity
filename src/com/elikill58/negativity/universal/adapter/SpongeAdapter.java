package com.elikill58.negativity.universal.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

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
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemBuilder;
import com.elikill58.negativity.sponge.impl.item.SpongeItemRegistrar;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.plugin.SpongeExternalPlugin;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.logger.Slf4jLoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeAdapter extends Adapter {

	private final LoggerAdapter logger;
	private final SpongeNegativity plugin;
	private final ConfigAdapter config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpongeNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final SpongeItemRegistrar itemRegistrar;

	public SpongeAdapter(SpongeNegativity sn, ConfigAdapter config) {
		this.plugin = sn;
		this.logger = new Slf4jLoggerAdapter(sn.getLogger());
		this.config = config;
		this.translationProviderFactory = new NegativityTranslationProviderFactory(sn.getDataFolder().resolve("messages"), "Negativity", "CheatHover");
		this.itemRegistrar = new SpongeItemRegistrar();
	}

	@Override
	public String getName() {
		return "sponge";
	}

	@Override
	public ConfigAdapter getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder().toFile();
	}

	@Override
	public void debug(String msg) {
		if(UniversalUtils.DEBUG)
			logger.info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) throws IOException {
		Asset asset = plugin.getContainer().getAsset(name).orElse(null);
		return asset == null ? null : asset.getUrl().openStream();
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		UniversalUtils.init();
		plugin.reloadCommands();
		ProxyCompanionManager.updateForceDisabled(config.getBoolean("disableProxyIntegration"));
		SpongeNegativity.trySendProxyPing();
		SpongeNegativity.log = config.getBoolean("log_alerts");
		SpongeNegativity.log_console = config.getBoolean("log_alerts_in_console");
		SpongeNegativity.hasBypass = config.getBoolean("Permissions.bypass.active");
	}

	@Override
	public String getVersion() {
		return Sponge.getPlatform().getMinecraftVersion().getName();
	}

	@Override
	public void reloadConfig() {
		try {
			this.config.load();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to reload configuration", e);
		}
		plugin.loadConfig();
		plugin.loadItemBypasses();
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
	public void alertMod(ReportType type, com.elikill58.negativity.api.entity.Player p, Cheat c, int reliability,
			String proof, CheatHover hover) {
		
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
	public Location createLocation(World w, double x, double y, double z) {
		return new SpongeLocation(w, x, y, z);
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover,
			String command) {
		((org.spongepowered.api.entity.living.player.Player) p).sendMessage(Text.builder(message).onHover(TextActions.showText(Text.of(hover))).build());
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
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpongeInventory(inventoryName, size, holder);
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
	public OfflinePlayer getOfflinePlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPlugin(String name) {
		return Sponge.getPluginManager().isLoaded(name);
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpongeExternalPlugin(Sponge.getPluginManager().getPlugin(name).orElse(null));
	}
}
