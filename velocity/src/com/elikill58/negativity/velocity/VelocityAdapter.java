package com.elikill58.negativity.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.ProxyAdapter;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.velocity.impl.entity.VelocityPlayer;
import com.elikill58.negativity.velocity.impl.plugin.VelocityExternalPlugin;
import com.google.gson.Gson;

public class VelocityAdapter extends ProxyAdapter {
	
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Proxy();
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private Configuration config;
	private final VelocityNegativity pl;
	
	public VelocityAdapter(VelocityNegativity pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config_bungee.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().toPath().resolve("lang"), "NegativityProxy", "CheatHover");
		this.logger = new Slf4jLoggerAdapter(pl.getLogger());
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.VELOCITY;
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
	public LoggerAdapter getLogger() {
		return logger;
	}
	
	@Override
	public void debug(String msg) {
		if (getConfig().getBoolean("debug", false))
			getLogger().info(msg);
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
		return pl.getServer().getVersion().getVersion();
	}
	
	@Override
	public String getPluginVersion() {
		return pl.getContainer().getDescription().getVersion().orElse("unknown");
	}
	
	@Override
	public void reloadConfig() {
		config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config_bungee.yml");
	}
	
	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}
	
	@Override
	public void runConsoleCommand(String cmd) {
		pl.getServer().getCommandManager().executeAsync(pl.getServer().getConsoleCommandSource(), cmd).join();
	}
	
	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Gson gson = new Gson();
				Map<?, ?> data = gson.fromJson(response, Map.class);
				Object isMcleaks = data.get("isMcleaks");
				if (isMcleaks != null) {
					return Boolean.parseBoolean(isMcleaks.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		});
	}
	
	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		pl.getServer().getAllPlayers().forEach((p) -> list.add(p.getUniqueId()));
		return list;
	}
	
	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		pl.getServer().getAllPlayers().forEach((p) -> list.add(NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer()));
		return list;
	}
	
	@Override
	public @Nullable Player getPlayer(String name) {
		return pl.getServer().getPlayer(name)
			.map(player -> NegativityPlayer.getNegativityPlayer(player.getUniqueId(), () -> new VelocityPlayer(player)).getPlayer())
			.orElse(null);
	}
	
	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return pl.getServer().getPlayer(uuid)
			.map(player -> NegativityPlayer.getNegativityPlayer(uuid, () -> new VelocityPlayer(player)).getPlayer())
			.orElse(null);
	}
	
	@Override
	public boolean hasPlugin(String name) {
		return pl.getServer().getPluginManager().isLoaded(name);
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		return new VelocityExternalPlugin(pl.getServer().getPluginManager().getPlugin(name).orElse(null));
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return pl.getServer().getPluginManager().getPlugins().stream()
			.filter(plugin -> plugin.getDescription().getDependency("negativity").isPresent())
			.map(VelocityExternalPlugin::new)
			.collect(Collectors.toList());
	}
	
	@Override
	public void runSync(Runnable call) {
		pl.getServer().getScheduler().buildTask(pl, call);
	}
}
