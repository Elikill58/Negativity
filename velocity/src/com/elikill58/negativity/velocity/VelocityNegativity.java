package com.elikill58.negativity.velocity;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

@Plugin(id = "negativity")
public class VelocityNegativity {

	public static final LegacyChannelIdentifier NEGATIVITY_CHANNEL_ID = new LegacyChannelIdentifier(NegativityMessagesManager.CHANNEL_ID);
	
    private final ProxyServer server;
    private final Logger logger;
    private final PluginContainer container;

    @Inject
    public VelocityNegativity(ProxyServer server, Logger logger, PluginContainer container) {
        this.server = server;
        this.logger = logger;
		this.container = container;
	}

    public ProxyServer getServer() {
    	return server;
    }

    public Logger getLogger() {
    	return logger;
    }

	public PluginContainer getContainer() {
		return container;
	}

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    	getLogger().info("Loading Negativity");
	    server.getEventManager().register(this, new VelocityListeners());
	    server.getChannelRegistrar().register(NEGATIVITY_CHANNEL_ID);
	    server.getCommandManager().register(new VNegativityCommand(), "vnegativity");
	    
		Adapter.setAdapter(new VelocityAdapter(this));
		Negativity.loadNegativity();

		NegativityAccountStorage.setDefaultStorage("database");

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

    public final InputStream getResourceAsStream(final String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public final File getDataFolder() {
        return new File("./plugins/Negativity");
    }
}
