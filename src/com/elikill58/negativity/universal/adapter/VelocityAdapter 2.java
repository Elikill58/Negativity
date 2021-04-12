package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.logger.Slf4jLoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.velocity.VelocityNegativity;
import com.elikill58.negativity.velocity.VelocityNegativityPlayer;
import com.google.gson.Gson;
import com.velocitypowered.api.proxy.Player;

public class VelocityAdapter extends Adapter {

	private ConfigAdapter config;
	private VelocityNegativity pl;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Proxy();
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;

	public VelocityAdapter(VelocityNegativity pl, ConfigAdapter config) {
		this.pl = pl;
		this.config = config;
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().toPath().resolve("lang"), "NegativityProxy", "CheatHover");
		this.logger = new Slf4jLoggerAdapter(pl.getLogger());
	}

	@Override
	public String getName() {
		return "velocity";
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
			getLogger().info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) {
		return pl.getResourceAsStream("assets/negativity/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public List<Cheat> getAbstractCheats() {
		return new ArrayList<>();
	}

	@Override
	public void reload() {
		reloadConfig();
		UniversalUtils.init();
	}

	@Override
	public String getVersion() {
		return pl.getServer().getVersion().getVersion();
	}

	@Override
	public void reloadConfig() {
		try {
			getConfig().load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		Optional<Player> player = pl.getServer().getPlayer(playerId);
		return player.isPresent() ? VelocityNegativityPlayer.getNegativityPlayer(player.get()) : null;
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, CheatHover hover) {}

	@Override
	public void runConsoleCommand(String cmd) {
		pl.getServer().getCommandManager().execute(pl.getServer().getConsoleCommandSource(), cmd);
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
	public List<UUID> getOnlinePlayers() {
		List<UUID> list = new ArrayList<>();
		for(Player temp : VelocityNegativity.getInstance().getServer().getAllPlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}
	
	@Override
	public void runAsync(Runnable call) {
		new Thread(call).start();
	}
}
