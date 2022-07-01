package com.elikill58.negativity.bungee;

import java.util.UUID;

import org.bstats.bungeecord.MetricsLite;

import com.elikill58.negativity.bungee.integrations.RedisSupport;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeNegativity extends Plugin {

	private static boolean redisBungee = false;
	public static boolean isRedisBungee() {
		return redisBungee;
	}
	
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

		if(redisBungee = getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
			getProxy().registerChannel("RedisBungee");
			RedisSupport.load(this);
			getLogger().info("Loaded RedisBungee support.");
		}
		
		try {
			Stats.sendStartupStats(ProxyServer.getInstance().getConfigurationAdapter().getListeners().iterator().next().getQueryPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		Negativity.closeNegativity();
	}
	
	public static String getProxyId() {
		return redisBungee ? RedisSupport.getProxyId() : "proxy";
	}
	
	public static String getNameFromUUID(UUID uuid) {
		if(redisBungee) {
			String redisName = RedisSupport.getPlayerName(uuid);
			if(redisName != null)
				return redisName;
		}
		return ProxyServer.getInstance().getPlayer(uuid).getName();
	}
	
	public static String getServerNameForPlayer(UUID uuid) {
		if(redisBungee) {
			String redisName = RedisSupport.getServerNameForPlayer(uuid);
			if(redisName != null)
				return redisName;
		}
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
		return p == null ? "" : getServerName(p.getServer().getInfo());
	}
	
	public static String getServerName(ServerInfo info) {
		return info == null ? "" : info.getName();
	}
}
