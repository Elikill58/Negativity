package com.elikill58.negativity.sponge8;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.common.timers.ActualizeInvTimer;
import com.elikill58.negativity.common.timers.AnalyzePacketTimer;
import com.elikill58.negativity.common.timers.SpawnFakePlayerTimer;
import com.elikill58.negativity.sponge8.listeners.BlockListeners;
import com.elikill58.negativity.sponge8.listeners.EntityListeners;
import com.elikill58.negativity.sponge8.listeners.FightManager;
import com.elikill58.negativity.sponge8.listeners.InventoryListeners;
import com.elikill58.negativity.sponge8.listeners.NegativityCommandWrapper;
import com.elikill58.negativity.sponge8.listeners.PlayersListeners;
import com.elikill58.negativity.sponge8.packets.NegativityPacketManager;
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
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.inject.Inject;

@Plugin("negativity")
public class SpongeNegativity {
	
	private static SpongeNegativity INSTANCE;

	private final Logger logger;
	private final PluginContainer container;
	private NegativityPacketManager packetManager;
	private final Path configDir;
	
	private RawDataChannel channel = null, bungeecordChannel = null;
	
	public RawDataChannel getBungeecordChannel() {
		return bungeecordChannel;
	}
	
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
		this.channel = Sponge.channelManager().ofType(ResourceKey.resolve(NegativityMessagesManager.CHANNEL_ID), RawDataChannel.class);
		this.bungeecordChannel = Sponge.channelManager().ofType(ResourceKey.resolve("bungeecord"), RawDataChannel.class);
	}
	
	@Listener
	public void onStartingEngine(StartingEngineEvent<Server> event) {
		Negativity.loadNegativity();
		NegativityAccountStorage.setDefaultStorage("file");
		
		EventManager eventManager = Sponge.eventManager();
		eventManager.registerListeners(this.container, new FightManager());
		eventManager.registerListeners(this.container, new BlockListeners());
		eventManager.registerListeners(this.container, new EntityListeners());
		eventManager.registerListeners(this.container, new InventoryListeners());
		eventManager.registerListeners(this.container, new PlayersListeners());
		
		schedule(new ActualizeInvTimer(), 5, null);
		schedule(new AnalyzePacketTimer(), 20, "negativity-packets");
		schedule(new SpawnFakePlayerTimer(), 20 * 60 * 10, null);
		
		if (SpongeUpdateChecker.isUpdateAvailable()) {
			logger.info("New version available ({}) : {}", SpongeUpdateChecker.getVersionString(), SpongeUpdateChecker.getDownloadUrl());
		}
	}

	@Listener
	public void onLoadedGame(LoadedGameEvent event) {
		packetManager = new NegativityPacketManager(this);
		Stats.sendStartupStats(Sponge.server().boundAddress().map(InetSocketAddress::getPort).orElse(-1));
		logger.info("Negativity v{} fully started !", container.metadata().version());
	}
	
	@Listener
	public void onRefreshGame(RefreshGameEvent event) {
		Adapter.getAdapter().reload();
	}
	
	@Listener
	public void onCommandRegistration(RegisterCommandEvent<Command.Raw> event) {
		loadCommands(event);
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
			//taskBuilder.name(name);
		}
		Sponge.server().scheduler().submit(taskBuilder.build());
	}
	
	private void loadCommands(RegisterCommandEvent<Command.Raw> event) {
		// TODO support commands reloading
		registerCommand(event, "negativity", "neg", "n");
		
		Configuration config = Adapter.getAdapter().getConfig();
		if (config.getBoolean("commands.mod")) {
			registerCommand(event, "nmod", "mod");
		}
		if (config.getBoolean("commands.kick")) {
			registerCommand(event, "nkick", "kick");
		}
		if (config.getBoolean("commands.lang")) {
			registerCommand(event, "nlang", "lang");
		}
		if (config.getBoolean("commands.report")) {
			registerCommand(event, "nreport", "report");
		}
		
		// BanManager#init is not called yet, so we have to work around it
		Configuration banConfig = UniversalUtils.loadConfig(new File(Adapter.getAdapter().getDataFolder(), "bans.yml"), "bans.yml");
		if (banConfig.getBoolean("commands.ban")) {
			registerCommand(event, "nban", "negban", "ban");
		}
		if (banConfig.getBoolean("commands.unban")) {
			registerCommand(event, "nunban", "negunban", "unban");
		}
	}
	
	private void registerCommand(RegisterCommandEvent<Command.Raw> event, String command, String... alias) {
		event.register(this.container, new NegativityCommandWrapper(command), command, alias);
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
	
	public NegativityPacketManager getPacketManager() {
		return packetManager;
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
		Iterator<ServerPlayer> onlinePlayers = Sponge.server().onlinePlayers().iterator();
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
