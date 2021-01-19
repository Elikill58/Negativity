package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.json.JSONObject;
import com.elikill58.json.parser.JSONParser;
import com.elikill58.json.parser.ParseException;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.config.BukkitConfigAdapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.logger.JavaLoggerAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotAdapter extends Adapter {

	private JavaPlugin pl;
	private final ConfigAdapter config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpigotNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = new BukkitConfigAdapter.PluginConfig(pl);
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().toPath().resolve("lang"), "Negativity", "CheatHover");
		this.logger = new JavaLoggerAdapter(pl.getLogger());
	}

	@Override
	public String getName() {
		return "spigot";
	}

	@Override
	public ConfigAdapter getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void log(String msg) {
		getLogger().info(msg);
	}

	@Override
	public void warn(String msg) {
		getLogger().warn(msg);
	}

	@Override
	public void error(String msg) {
		getLogger().error(msg);
	}

	@Override
	public void debug(String msg) {
		if(UniversalUtils.isDebugMode())
			pl.getLogger().info("[Debug] " + msg);
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
		UniversalUtils.init();
		Cheat.loadCheat();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));
		SpigotNegativity.trySendProxyPing();
		SpigotNegativity.setupValue();
		for(Player p : Utils.getOnlinePlayers())
			SpigotNegativity.manageAutoVerif(p);
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		try {
			getConfig().load();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to reload configuration", e);
		}
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		Player player = Bukkit.getPlayer(playerId);
		return player != null ? SpigotNegativityPlayer.getNegativityPlayer(player) : null;
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {
		alertMod(type, proof, c, reliability, proof, new CheatHover.Literal(hover_proof));
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, CheatHover hover) {
		SpigotNegativity.alertMod(type, (Player) p, c, reliability, proof, hover);
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
	public List<UUID> getOnlinePlayers() {
		List<UUID> list = new ArrayList<>();
		for(Player temp : Utils.getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}
}
