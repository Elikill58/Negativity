package com.elikill58.negativity.bungee;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.BungeeAdapter;
import com.elikill58.negativity.universal.config.MD5ConfigAdapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeNegativity extends Plugin {

	private static BungeeNegativity instance;
	public static BungeeNegativity getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		new Metrics(this);

		getProxy().registerChannel(NegativityMessagesManager.CHANNEL_ID);
		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new NegativityListener());
		pluginManager.registerCommand(this, new BNegativityCommand());
		pluginManager.registerListener(this, new BNegativityCommand.TabCompleter());

		MD5ConfigAdapter.ByProvider config;
		try {
			config = new MD5ConfigAdapter.ByProvider(ConfigurationProvider.getProvider(YamlConfiguration.class),
					getDataFolder().toPath().resolve("config.yml"),
					() -> getResourceAsStream("bungee_config.yml"));
		} catch (IOException e) {
			throw new UncheckedIOException("Could not load configuration", e);
		}
		Adapter.setAdapter(new BungeeAdapter(this, config));
		UniversalUtils.init();

		NegativityAccountStorage.setDefaultStorage("database");

		Perm.registerChecker(Perm.PLATFORM_CHECKER, new BungeePermissionChecker());

		Stats.loadStats();
		Stats.updateStats(StatsType.ONLINE, 1 + "");
		try {
			Stats.updateStats(StatsType.PORT, ((LinkedHashMap<?, ?>) ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder().getParentFile().getParentFile(), "config.yml"))
							.getList("listeners").get(0)).get("query_port") + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
	}
}
