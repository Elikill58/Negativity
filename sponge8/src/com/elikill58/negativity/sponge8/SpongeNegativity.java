package com.elikill58.negativity.sponge8;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import com.elikill58.negativity.sponge8.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.google.inject.Inject;

@Plugin("negativity")
public class SpongeNegativity {
	
	private static SpongeNegativity INSTANCE;

	private final Logger logger;
	private final PluginContainer container;
	private final Path configDir;
	
	private RawDataChannel channel;
	
	@Inject
	public SpongeNegativity(Logger logger, PluginContainer container, @ConfigDir(sharedRoot = false) Path configDir) {
		this.logger = logger;
		this.container = container;
		this.configDir = configDir;
		INSTANCE = this;
	}
	
	@Listener
	public void onConstructPlugin(ConstructPluginEvent event) {
		Adapter.setAdapter(new SpongeAdapter(this));
		Negativity.loadNegativity();
		ResourceKey channelKey = ResourceKey.resolve(NegativityMessagesManager.CHANNEL_ID);
		this.channel = Sponge.getChannelRegistry().getOfType(channelKey, RawDataChannel.class);
	}
	
	@Listener
	public void onLoadedGame(LoadedGameEvent event) {
		logger.info("Hello from Negativity v{}", container.getMetadata().getVersion());
	}
	
	@Listener
	public void onRefreshGame(RefreshGameEvent event) {
		Adapter.getAdapter().reload();
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public PluginContainer getContainer() {
		return container;
	}
	
	public Path getConfigDir() {
		return configDir;
	}
	
	public static void sendPluginMessage(byte[] rawMessage) {
		ServerPlayer player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			INSTANCE.channel.play().sendTo(player, payload -> payload.writeBytes(rawMessage));
		} else {
			getInstance().getLogger().error("Could not send plugin message to proxy because there are no player online.");
		}
	}
	
	public static void sendProxyPing(ServerPlayer player) {
		ProxyCompanionManager.searchedCompanion = true;
		INSTANCE.channel.play().sendTo(player, buffer -> {
			try {
				buffer.writeBytes(NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION)));
			} catch (IOException ex) {
				INSTANCE.logger.error("Could not write ProxyPingMessage.", ex);
			}
		});
	}
	
	public static void trySendProxyPing() {
		Iterator<ServerPlayer> onlinePlayers = Sponge.getServer().getOnlinePlayers().iterator();
		if (onlinePlayers.hasNext()) {
			sendProxyPing(onlinePlayers.next());
		}
	}
	
	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}
	
	public static PluginContainer container() {
		return INSTANCE.container;
	}
}
