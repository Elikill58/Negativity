package com.elikill58.negativity.sponge8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.common.timers.ActualizeInvTimer;
import com.elikill58.negativity.common.timers.AnalyzePacketTimer;
import com.elikill58.negativity.common.timers.ClickManagerTimer;
import com.elikill58.negativity.common.timers.PendingAlertsTimer;
import com.elikill58.negativity.common.timers.SpawnFakePlayerTimer;
import com.elikill58.negativity.sponge8.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
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
		ResourceKey channelKey = ResourceKey.resolve(NegativityMessagesManager.CHANNEL_ID);
		this.channel = Sponge.getChannelRegistry().getOfType(channelKey, RawDataChannel.class);
	}
	
	@Listener
	public void onStartingEngine(StartingEngineEvent<Server> event) {
		Negativity.loadNegativity();
		NegativityAccountStorage.setDefaultStorage("file");
		
		schedule(new ClickManagerTimer(), 20, null);
		schedule(new ActualizeInvTimer(), 5, null);
		schedule(new AnalyzePacketTimer(), 20, "negativity-packets");
		schedule(new SpawnFakePlayerTimer(), 20 * 60 * 10, null);
		if (Negativity.timeBetweenAlert != -1) {
			schedule(new PendingAlertsTimer(), Negativity.timeBetweenAlert / 50, "negativity-pending-alerts");
		}
		
		if (SpongeUpdateChecker.isUpdateAvailable()) {
			logger.info("New version available ({}) : {}", SpongeUpdateChecker.getVersionString(), SpongeUpdateChecker.getDownloadUrl());
		}
	}

	@Listener
	public void onLoadedGame(LoadedGameEvent event) {
		Stats.sendStartupStats(Sponge.getServer().getBoundAddress().map(InetSocketAddress::getPort).orElse(-1));
		logger.info("Hello from Negativity v{}", container.getMetadata().getVersion());
	}
	
	@Listener
	public void onRefreshGame(RefreshGameEvent event) {
		Adapter.getAdapter().reload();
	}
	
	@Listener
	public void onStoppingEngine(StoppingEngineEvent<Server> event) {
		NegativityPlayer.getAllPlayers().values().forEach(NegativityPlayer::destroy);
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		Database.close();
	}
	
	private void schedule(Runnable task, int intervalTicks, @Nullable String name) {
		Task.Builder taskBuilder = Task.builder().execute(task).interval(Ticks.of(intervalTicks)).plugin(this.container);
		if (name != null) {
			taskBuilder.name(name);
		}
		Sponge.getServer().getScheduler().submit(taskBuilder.build());
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
