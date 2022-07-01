package com.elikill58.negativity.sponge;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
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
import org.spongepowered.api.network.EngineConnection;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.ChannelManager;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge.listeners.BlockListeners;
import com.elikill58.negativity.sponge.listeners.EntityListeners;
import com.elikill58.negativity.sponge.listeners.InventoryListeners;
import com.elikill58.negativity.sponge.listeners.NegativityCommandWrapper;
import com.elikill58.negativity.sponge.listeners.PlayersListeners;
import com.elikill58.negativity.sponge.packets.NegativityPacketManager;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
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
		ChannelManager chan = Sponge.channelManager();
		chan.ofType(ResourceKey.resolve("fml:hs"), RawDataChannel.class).play().addHandler(new FmlRawDataListener());
		this.channel = chan.ofType(ResourceKey.resolve(NegativityMessagesManager.CHANNEL_ID), RawDataChannel.class);
		this.channel.play().addHandler(new ProxyCompanionListener());
		this.bungeecordChannel = chan.ofType(ResourceKey.resolve("bungeecord"), RawDataChannel.class);
	}
	
	@Listener
	public void onStartingEngine(StartingEngineEvent<Server> event) {
		Negativity.loadNegativity();
		NegativityAccountStorage.setDefaultStorage("file");
		
		EventManager eventManager = Sponge.eventManager();
		eventManager.registerListeners(this.container, new BlockListeners());
		eventManager.registerListeners(this.container, new EntityListeners());
		eventManager.registerListeners(this.container, new InventoryListeners());
		eventManager.registerListeners(this.container, new PlayersListeners());
		
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
		Negativity.closeNegativity();
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

	private static class FmlRawDataListener implements RawPlayDataHandler<EngineConnection> {

		@Override
		public void handlePayload(ChannelBuf data, EngineConnection connection) {
			if (!(connection instanceof PlayerConnection))
				return;

			ServerPlayer player = (ServerPlayer) ((PlayerConnection) connection).player();
			byte[] rawData = data.readBytes(data.available());
			HashMap<String, String> playerMods = NegativityPlayer.getNegativityPlayer(player.uniqueId(), () -> new SpongePlayer(player)).mods;
			playerMods.clear();
			playerMods.putAll(Utils.getModsNameVersionFromMessage(new String(rawData, StandardCharsets.UTF_8)));
		}
	}

	private static class ProxyCompanionListener implements RawPlayDataHandler<EngineConnection> {

		@Override
		public void handlePayload(ChannelBuf data, EngineConnection connection) {
			if (!(connection instanceof PlayerConnection))
				return;
			byte[] rawData = data.readBytes(data.available());
			ServerPlayer p = (ServerPlayer) ((PlayerConnection) connection).player();
			com.elikill58.negativity.api.events.EventManager.callEvent(new GameChannelNegativityMessageEvent(SpongeEntityManager.getPlayer(p), rawData));
		}
	}
	
	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}
	
	public static PluginContainer container() {
		return INSTANCE.container;
	}
}
