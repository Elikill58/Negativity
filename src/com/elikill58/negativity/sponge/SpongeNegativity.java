package com.elikill58.negativity.sponge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.timers.AnalyzePacketTimer;
import com.elikill58.negativity.api.timers.PendingAlertsTimer;
import com.elikill58.negativity.sponge.commands.BanCommand;
import com.elikill58.negativity.sponge.commands.KickCommand;
import com.elikill58.negativity.sponge.commands.LangCommand;
import com.elikill58.negativity.sponge.commands.MigrateOldBansCommand;
import com.elikill58.negativity.sponge.commands.ModCommand;
import com.elikill58.negativity.sponge.commands.NegativityCommand;
import com.elikill58.negativity.sponge.commands.ReportCommand;
import com.elikill58.negativity.sponge.commands.UnbanCommand;
import com.elikill58.negativity.sponge.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge.listeners.BlockListeners;
import com.elikill58.negativity.sponge.listeners.EntityListeners;
import com.elikill58.negativity.sponge.listeners.FightManager;
import com.elikill58.negativity.sponge.listeners.InventoryListeners;
import com.elikill58.negativity.sponge.listeners.PlayersEventsManager;
import com.elikill58.negativity.sponge.listeners.PlayersListeners;
import com.elikill58.negativity.sponge.packets.NegativityPacketManager;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpongeAdapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;
import com.elikill58.negativity.universal.ban.processor.SpongeBanProcessor;
import com.elikill58.negativity.universal.bypass.ItemUseBypass;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.config.SpongeConfigAdapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.dataStorage.file.SpongeFileNegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.inject.Inject;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

@Plugin(id = "negativity", name = "Negativity", version = "1.9.2", description = "It's an Advanced AntiCheat Detection", authors = { "Elikill58", "RedNesto" }, dependencies = {
		@Dependency(id = "packetgate") })
public class SpongeNegativity {

	public static SpongeNegativity INSTANCE;

	@Inject
	private PluginContainer plugin;
	@Inject
	public Logger logger;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Path configFile;
	private ConfigAdapter config;
	private NegativityPacketManager packetManager;
	public static RawDataChannel channel = null, fmlChannel = null;

	private final Map<String, CommandMapping> reloadableCommands = new HashMap<>();
	private static int timeBetweenAlert = -1;

	public PluginContainer getContainer() {
		return plugin;
	}

	public static boolean hasPacketGate = false, hasPrecogs = false;

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		INSTANCE = this;
		configFile = configDir.resolve("config.conf");

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
		this.config = new SpongeConfigAdapter.ByLoader(logger, configLoader, configFile,
				() -> Sponge.getAssetManager().getAsset(this, "config.conf")
						.orElseThrow(() -> new IllegalStateException("Could not get default configuration file"))
						.getUrl().openStream());
		try {
			this.config.load();
		} catch (IOException e) {
			logger.error("Failed to load configuration", e);
		}
		Adapter.setAdapter(new SpongeAdapter(this, config));
		Negativity.loadNegativity();
		loadConfig();
		EventManager eventManager = Sponge.getEventManager();
		eventManager.registerListeners(this, new FightManager());
		eventManager.registerListeners(this, new PlayersEventsManager());
		eventManager.registerListeners(this, new BlockListeners());
		// TODO add Commands listeners
		//eventManager.registerListeners(this, new CommandsListeners());
		eventManager.registerListeners(this, new EntityListeners());
		eventManager.registerListeners(this, new InventoryListeners());
		eventManager.registerListeners(this, new PlayersListeners());
		
		Task.builder().execute(new AnalyzePacketTimer()).delayTicks(0).interval(1, TimeUnit.SECONDS)
				.name("negativity-packets").submit(this);
		if(timeBetweenAlert != -1) // is == -1, don't need timer
			Task.builder().execute(new PendingAlertsTimer()).interval(timeBetweenAlert, TimeUnit.MILLISECONDS)
					.name("negativity-pending-alerts").submit(this);
		plugin.getLogger().info("Negativity v" + plugin.getVersion().get() + " loaded.");

		NegativityAccountStorage.register("file", new SpongeFileNegativityAccountStorage(configDir.resolve("user")));
		NegativityAccountStorage.setDefaultStorage("file");

		BanManager.registerProcessor("sponge", new SpongeBanProcessor());
		BanManager.registerProcessor(ForwardToProxyBanProcessor.PROCESSOR_ID, new ForwardToProxyBanProcessor(SpongeNegativity::sendPluginMessage));

		if(SpongeUpdateChecker.ifUpdateAvailable()) {
			getLogger().info("New version available (" + SpongeUpdateChecker.getVersionString() + ") : " + SpongeUpdateChecker.getDownloadUrl());
		}

		if (!ProxyCompanionManager.isIntegrationEnabled())
			Task.builder().async().delayTicks(1).execute(new Runnable() {
				@Override
				public void run() {
					try {
						Stats.loadStats();
						Stats.updateStats(StatsType.ONLINE, 1 + "");
						Stats.updateStats(StatsType.PORT, Sponge.getServer().getBoundAddress().get().getPort() + "");
					} catch (Exception e) {

					}
				}
			}).submit(this);
	}

	@Listener
	public void onGameStop(GameStoppingServerEvent e) {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			NegativityPlayer.getCached(player.getUniqueId()).destroy();
		}
		if (!ProxyCompanionManager.isIntegrationEnabled()) {
			Task.builder().async().delayTicks(1).execute(new Runnable() {
				@Override
				public void run() {
					Stats.updateStats(StatsType.ONLINE, 0 + "");
				}
			}).submit(this);
		}
		Database.close();
	}

	@Listener
	public void onGameStart(GameStartingServerEvent e) {
		loadItemBypasses();
		packetManager = new NegativityPacketManager(this);
		try {
			Class.forName("com.me4502.precogs.Precogs");
			hasPrecogs = true;
		} catch (ClassNotFoundException e1) {
			hasPrecogs = false;
		}
		try {
			Class.forName("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher");
			SpongeForgeSupport.isOnSpongeForge = true;
		} catch (ClassNotFoundException e1) {
			SpongeForgeSupport.isOnSpongeForge = false;
		}

		loadCommands(false);

		channel = Sponge.getChannelRegistrar().createRawChannel(this, NegativityMessagesManager.CHANNEL_ID);
		channel.addListener(new ProxyCompanionListener());
		if (Sponge.getChannelRegistrar().isChannelAvailable("FML|HS")) {
			fmlChannel = Sponge.getChannelRegistrar().getOrCreateRaw(this, "FML|HS");
			fmlChannel.addListener(new FmlRawDataListener());
		}
	}

	public void reloadCommands() {
		loadCommands(true);
	}

	private void loadCommands(boolean reload) {
		CommandManager cmd = Sponge.getCommandManager();

		if (!reload) {
			cmd.register(this, NegativityCommand.create(), "negativity", "neg", "n");
			cmd.register(this, MigrateOldBansCommand.create(), "negativitymigrateoldbans");
		}

		reloadCommand("mod", cmd, ModCommand::create, "nmod", "mod");
		reloadCommand("kick", cmd, KickCommand::create, "nkick", "kick");
		reloadCommand("lang", cmd, LangCommand::create, "nlang", "lang");
		reloadCommand("report", cmd, ReportCommand::create, "nreport", "report", "repot");
		reloadCommand("ban", cmd, BanCommand::create, "nban", "negban", "ban");
		reloadCommand("unban", cmd, UnbanCommand::create, "nunban", "negunban", "unban");
	}

	private void reloadCommand(String configKey, CommandManager manager, Supplier<CommandCallable> command, String... aliases) {
		reloadCommand(configKey, config.getChild("commands").getBoolean(configKey), manager, command, aliases);
	}

	private void reloadCommand(String mappingKey, boolean enabled, CommandManager manager, Supplier<CommandCallable> command, String... aliases) {
		if (enabled) {
			if (!reloadableCommands.containsKey(mappingKey)) {
				manager.register(this, command.get(), aliases).ifPresent(mapping -> reloadableCommands.put(mappingKey, mapping));
			}
		} else {
			CommandMapping mapping = reloadableCommands.remove(mappingKey);
			if (mapping != null) {
				manager.removeMapping(mapping);
			}
		}
	}

	@Listener
	public void onGameReload(GameReloadEvent event) {
		Adapter.getAdapter().reload();
	}

	@Listener
	public void onAuth(ClientConnectionEvent.Auth e) {
		UUID playerId = e.getProfile().getUniqueId();
		NegativityAccount account = NegativityAccount.get(playerId);
		Ban activeBan = BanManager.getActiveBan(playerId);
		if (activeBan != null) {
			String kickMsgKey;
			String formattedExpiration;
			if (activeBan.isDefinitive()) {
				kickMsgKey = "ban.kick_def";
				formattedExpiration = "definitively";
			} else {
				kickMsgKey = "ban.kick_time";
				LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(activeBan.getExpirationTime()), ZoneId.systemDefault());
				formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(expirationDateTime);
			}
			e.setCancelled(true);
			e.setMessage(Messages.getMessage(account, kickMsgKey, "%reason%", activeBan.getReason(), "%time%" , formattedExpiration, "%by%", activeBan.getBannedBy()));
			Adapter.getAdapter().getAccountManager().dispose(account.getPlayerId());
		}
	}

	@Listener
	public void onJoin(ClientConnectionEvent.Join e, @First Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpongePlayer(p));
		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
			if (!hasPacketGate) {
				try {
					p.sendMessage(Text.builder("[Negativity] Dependency not found. Please, download it here.")
							.onHover(TextActions.showText(Text.of("Click here")))
							.onClick(
									TextActions.openUrl(new URL("https://github.com/CrushedPixel/PacketGate/releases")))
							.color(TextColors.RED).build());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e, @First Player p) {
		Task.builder().delayTicks(5).execute(() -> {
			NegativityPlayer.removeFromCache(p.getUniqueId());
			NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
			UUID playerId = p.getUniqueId();
			accountManager.save(playerId);
			accountManager.dispose(playerId);
		}).submit(this);
	}

	public void loadConfig() {
		timeBetweenAlert = config.getInt("time_between_alert");
	}

	public void loadItemBypasses() {
		ItemUseBypass.ITEM_BYPASS.clear();
		ConfigAdapter allItemsConfig = config.getChild("items");
		for (String key : allItemsConfig.getKeys()) {
			ConfigAdapter itemConfig = allItemsConfig.getChild(key);
			new ItemUseBypass(key, itemConfig.getString("cheats"), itemConfig.getString("when"));
		}
	}

	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}

	public static Text createAlertText(Player suspect, Cheat cheat, String hoverProof, int ping, int pendingAlertsCount,
									   String messageKey, int reliability, MessageReceiver receiver) {
		return Text
				.builder(Messages.getStringMessage(receiver, messageKey,
						"%name%", suspect.getName(),
						"%cheat%", cheat.getName(),
						"%reliability%", String.valueOf(reliability),
						"%nb%", String.valueOf(pendingAlertsCount)))
				.onClick(TextActions.runCommand("/negativity " + suspect.getName()))
				.onHover(TextActions.showText(
						Text.of(Messages.getStringMessage(receiver, "negativity.alert_hover",
								"%reliability%", String.valueOf(reliability),
								"%ping%", String.valueOf(ping)),
								TextColors.RESET, (hoverProof.isEmpty() ? "" : "\n\n" + hoverProof))))
				.build();
	}

	public Path getDataFolder() {
		return configDir;
	}

	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}

	public static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount) {
		channel.sendTo(p, (payload) -> {
			try {
				AlertMessage message = new AlertMessage(p.getName(), cheatName, reliability, ping, hover, alertsCount);
				payload.writeBytes(NegativityMessagesManager.writeMessage(message));
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Could not send alert message to the proxy.", e);
			}
		});
	}

	public static void sendReportMessage(Player p, String reportMsg, String nameReported) {
		channel.sendTo(p, (payload) -> {
			try {
				ReportMessage message = new ReportMessage(nameReported, reportMsg, p.getName());
				payload.writeBytes(NegativityMessagesManager.writeMessage(message));
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Could not send report message to the proxy.", e);
			}
		});
	}

	public static void sendProxyPing(Player player) {
		ProxyCompanionManager.searchedCompanion = true;
		channel.sendTo(player, (buffer) -> {
			try {
				buffer.writeBytes(NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION)));
			} catch (IOException ex) {
				SpongeNegativity.getInstance().getLogger().error("Could not write ProxyPingMessage.", ex);
			}
		});
	}

	public static void sendPluginMessage(byte[] rawMessage) {
		Player player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			channel.sendTo(player, payload -> payload.writeBytes(rawMessage));
		} else {
			getInstance().getLogger().error("Could not send plugin message to proxy because there are no player online.");
		}
	}

	public static void trySendProxyPing() {
		Iterator<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers().iterator();
		if (onlinePlayers.hasNext()) {
			sendProxyPing(onlinePlayers.next());
		}
	}

	private static class FmlRawDataListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf channelBuf, RemoteConnection connection, Type side) {
			if (!(connection instanceof PlayerConnection)) {
				return;
			}

			Player player = ((PlayerConnection) connection).getPlayer();
			byte[] rawData = channelBuf.readBytes(channelBuf.available());
			HashMap<String, String> playerMods = NegativityPlayer.getNegativityPlayer(player.getUniqueId(), () -> new SpongePlayer(player)).MODS;
			playerMods.clear();
			playerMods.putAll(Utils.getModsNameVersionFromMessage(new String(rawData, StandardCharsets.UTF_8)));
		}
	}

	private static class ProxyCompanionListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {
			byte[] rawData = data.readBytes(data.available());
			NegativityMessage message;
			try {
				message = NegativityMessagesManager.readMessage(rawData);
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Failed to read proxy companion message.", e);
				return;
			}

			if (message instanceof ProxyPingMessage) {
				ProxyPingMessage pingMessage = (ProxyPingMessage) message;
				ProxyCompanionManager.foundCompanion(pingMessage.getProtocol());
			}
		}
	}
}
