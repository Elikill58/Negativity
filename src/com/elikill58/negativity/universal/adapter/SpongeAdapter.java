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

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeAdapter extends Adapter {

	private final Logger logger;
	private final SpongeNegativity plugin;
	private ConfigAdapter config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpongeNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;

	public SpongeAdapter(SpongeNegativity sn, ConfigAdapter config) {
		this.plugin = sn;
		this.logger = sn.getLogger();
		this.config = config;
		this.translationProviderFactory = new NegativityTranslationProviderFactory(sn.getDataFolder().resolve("messages"), "Negativity", "CheatHover");
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
	public void log(String msg) {
		logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
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

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		Player player = Sponge.getServer().getPlayer(playerId).orElse(null);
		return player != null ? SpongeNegativityPlayer.getNegativityPlayer(player) : null;
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {
		alertMod(type, (Player) p, c, reliability, proof, new CheatHover.Literal(hover_proof));
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, CheatHover hover) {
		SpongeNegativity.alertMod(type, (Player) p, c, reliability, proof, hover);
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
	public List<UUID> getOnlinePlayers() {
		List<UUID> list = new ArrayList<>();
		for(Player temp : Sponge.getServer().getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}
}
