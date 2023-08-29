package com.elikill58.negativity.bungee;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.StringJoiner;

import com.elikill58.deps.md_5.config.ConfigurationProvider;
import com.elikill58.deps.md_5.config.YamlConfiguration;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.BungeeAdapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.support.AdvancedBanProcessor;
import com.elikill58.negativity.universal.ban.support.LiteBansProcessor;
import com.elikill58.negativity.universal.config.MD5ConfigAdapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

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

		MD5ConfigAdapter.ByProvider config = new MD5ConfigAdapter.ByProvider(ConfigurationProvider.getProvider(YamlConfiguration.class),
				getDataFolder().toPath().resolve("config.yml"),
				() -> getResourceAsStream("bungee_config.yml"));
		try {
			config.load();
		} catch (IOException e) {
			throw new UncheckedIOException("Could not load configuration", e);
		}
		Adapter.setAdapter(new BungeeAdapter(this, config));
		UniversalUtils.init();

		NegativityAccountStorage.setDefaultStorage("database");


		StringJoiner supportedPluginName = new StringJoiner(", ");
		
		if (getProxy().getPluginManager().getPlugin("AdvancedBan") != null) {
			BanManager.registerProcessor("advancedban", new AdvancedBanProcessor());
			supportedPluginName.add("AdvancedBan");
		}

		if (getProxy().getPluginManager().getPlugin("LiteBans") != null) {
			BanManager.registerProcessor("litebans", new LiteBansProcessor());
			supportedPluginName.add("LiteBans");
		}
		
		if (supportedPluginName.length() > 0) {
			getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}
		
		Perm.registerChecker(Perm.PLATFORM_CHECKER, new BungeePermissionChecker());

		Stats.loadStats();
	}

	@Override
	public void onDisable() {
		Database.close();
	}
}
