package com.elikill58.negativity.bungee;

import org.bstats.bungeecord.MetricsLite;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeNegativity extends Plugin {

	@Override
	public void onEnable() {
		Adapter.setAdapter(new BungeeAdapter(this));

		new MetricsLite(this, 3510);

		getProxy().registerChannel(NegativityMessagesManager.CHANNEL_ID);
		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BungeeListeners());
		pluginManager.registerCommand(this, new BNegativityCommand());
		pluginManager.registerListener(this, new BNegativityCommand.TabCompleter());
		
		Negativity.loadNegativity();

		NegativityAccountStorage.setDefaultStorage("database");

		try {
			int port = ProxyServer.getInstance().getConfig().getListeners().iterator().next().getQueryPort();
			Stats.sendStartupStats(port);
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
