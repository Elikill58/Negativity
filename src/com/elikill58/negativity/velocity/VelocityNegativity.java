package com.elikill58.negativity.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.VelocityAdapter;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

@Plugin(id = "negativity", name = "Negativity", version = "1.6",
        description = "It's an Advanced AntiCheat Detection", authors = {"Elikill58", "RedNesto"})
public class VelocityNegativity {

	public static final LegacyChannelIdentifier NEGATIVITY_CHANNEL_ID = new LegacyChannelIdentifier(NegativityMessagesManager.CHANNEL_ID);

	public static Configuration CONFIG;
	private static VelocityNegativity instance;
	public static VelocityNegativity getInstance() {
		return instance;
	}

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityNegativity(ProxyServer server, Logger logger) {
		instance = this;
		
        this.server = server;
        this.logger = logger;
    }
    
    public ProxyServer getServer() {
    	return server;
    }
    
    public Logger getLogger() {
    	return logger;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    	getLogger().info("Loading Negativity");
		enableConfig();
	    server.getEventManager().register(this, new NegativityListener());
		server.getChannelRegistrar().register(NEGATIVITY_CHANNEL_ID);

		Adapter.setAdapter(new VelocityAdapter(this, CONFIG));
		UniversalUtils.init();
		Stats.loadStats();
		Stats.updateStats(StatsType.ONLINE, 1 + "");
		try {
			Stats.updateStats(StatsType.PORT, getServer().getBoundAddress().getPort() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	getLogger().info("Negativity enabled");
	}

    @Subscribe
    public void onProxyDisable(ProxyShutdownEvent e) {
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
	}
    
	protected boolean enableConfig() {
		File folder = getDataFolder();
		folder.mkdir();
		File resourceFile = new File(folder, "config.yml");
		try {
			if (!resourceFile.exists()) {
				resourceFile.createNewFile();
				try (InputStream in = getResourceAsStream("bungee_config.yml");
						OutputStream out = new FileOutputStream(resourceFile)) {
					ByteStreams.copy(in, out);
				}
			}
			CONFIG = ConfigurationProvider.getProvider(YamlConfiguration.class).load(resourceFile);
	    	getLogger().info("Configuration loaded");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    
    public final InputStream getResourceAsStream(final String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
    
    public final File getDataFolder() {
        return new File("./plugins/Negativity");
    }
}
