package com.elikill58.negativity.bungee;

import org.bstats.bungeecord.MetricsLite;

import com.elikill58.negativity.bungee.integrations.RedisSupport;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.RedisNegativityMessage;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeNegativity extends Plugin {

	private static boolean hasRedisBungee = false;
	
	@Override
	public void onEnable() {
		Adapter.setAdapter(new BungeeAdapter(this));

		new MetricsLite(this, 3510);

		getProxy().registerChannel(NegativityMessagesManager.CHANNEL_ID);
		getProxy().registerChannel("RedisBungee");
		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BungeeListeners());
		pluginManager.registerCommand(this, new BNegativityCommand());
		pluginManager.registerListener(this, new BNegativityCommand.TabCompleter());
		
		Negativity.loadNegativity();

		NegativityAccountStorage.setDefaultStorage("database");

		if(hasRedisBungee = getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
			RedisSupport.load(this);
			getLogger().info("Loaded RedisBungee support.");
		}
		
		try {
			@SuppressWarnings("deprecation")
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
	
	public static void sendRedisMessageIfNeed(RedisNegativityMessage redisMsg) {
		if(hasRedisBungee)
			RedisSupport.sendMessage(redisMsg);
	}
	
	public static String getProxyId() {
		return hasRedisBungee ? RedisSupport.getProxyId() : "proxy";
	}
}
