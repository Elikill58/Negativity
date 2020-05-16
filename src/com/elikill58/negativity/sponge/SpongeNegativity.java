package com.elikill58.negativity.sponge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
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
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.commands.BanCommand;
import com.elikill58.negativity.sponge.commands.KickCommand;
import com.elikill58.negativity.sponge.commands.LangCommand;
import com.elikill58.negativity.sponge.commands.MigrateOldBansCommand;
import com.elikill58.negativity.sponge.commands.ModCommand;
import com.elikill58.negativity.sponge.commands.NegativityCommand;
import com.elikill58.negativity.sponge.commands.ReportCommand;
import com.elikill58.negativity.sponge.commands.UnbanCommand;
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.listeners.FightManager;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;
import com.elikill58.negativity.sponge.listeners.PlayersEventsManager;
import com.elikill58.negativity.sponge.packets.PacketGateManager;
import com.elikill58.negativity.sponge.timers.ActualizerTimer;
import com.elikill58.negativity.sponge.timers.PacketsTimers;
import com.elikill58.negativity.sponge.timers.PendingAlertsTimer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpongeAdapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;
import com.elikill58.negativity.universal.ban.processor.SpongeBanProcessor;
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

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

@Plugin(id = "negativity", name = "Negativity", version = "1.7", description = "It's an Advanced AntiCheat Detection", authors = { "Elikill58", "RedNesto" }, dependencies = {
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
	public static RawDataChannel channel = null, fmlChannel = null;

	private final Map<String, CommandMapping> reloadableCommands = new HashMap<>();
	private static int timeBetweenAlert = -1;

	public PluginContainer getContainer() {
		return plugin;
	}

	public static boolean log = true, log_console = true, hasPacketGate = false, hasPrecogs = false, hasBypass = false, viaVersionSupport = false,
			essentialsSupport = false;

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		INSTANCE = this;
		configFile = configDir.resolve("config.conf");

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
		CommentedConfigurationNode rootConfigNode;
		try {
			rootConfigNode = configLoader.load();
		} catch (IOException e) {
			logger.error("Failed to load configuration", e);
			rootConfigNode = configLoader.createEmptyNode();
		}
		this.config = new SpongeConfigAdapter.ByLoader(rootConfigNode, logger, configLoader, configFile,
				() -> Sponge.getAssetManager().getAsset(this, "config.conf")
						.orElseThrow(() -> new IllegalStateException("Could not get default configuration file"))
						.getUrl().openStream());
		Adapter.setAdapter(new SpongeAdapter(this, config));
		UniversalUtils.init();
		loadConfig();
		Cheat.loadCheat();
		EventManager eventManager = Sponge.getEventManager();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			if(c.hasListener())
				eventManager.registerListeners(this, c);
		}
		eventManager.registerListeners(this, new FightManager());
		eventManager.registerListeners(this, new PlayersEventsManager());
		
		Task.builder().execute(new PacketsTimers()).delayTicks(0).interval(1, TimeUnit.SECONDS)
				.name("negativity-packets").submit(this);
		Task.builder().execute(new ActualizerTimer()).interval(1, TimeUnit.SECONDS)
				.name("negativity-actualizer").submit(this);
		Task.builder().execute(new PendingAlertsTimer()).interval(timeBetweenAlert, TimeUnit.MILLISECONDS)
				.name("negativity-pending-alerts").submit(this);
		plugin.getLogger().info("Negativity v" + plugin.getVersion().get() + " loaded.");

		NegativityAccountStorage.register("file", new SpongeFileNegativityAccountStorage(configDir.resolve("user")));
		NegativityAccountStorage.setDefaultStorage("file");

		BanManager.registerProcessor("sponge", new SpongeBanProcessor());
		BanManager.registerProcessor(ForwardToProxyBanProcessor.PROCESSOR_ID, new ForwardToProxyBanProcessor(SpongeNegativity::sendPluginMessage));
		Perm.registerChecker(Perm.PLATFORM_CHECKER, new SpongePermissionChecker());

		if(SpongeUpdateChecker.ifUpdateAvailable()) {
			getLogger().info("New version available (" + SpongeUpdateChecker.getVersionString() + ") : " + SpongeUpdateChecker.getDownloadUrl());
		}
		/*Task.builder()
				.async()
				.name("Negativity Startup Updater Checker")
				.execute(() -> SpongeUpdateChecker.ifUpdateAvailable(result ->
						getLogger().info("New version available ({}): {}",
								result.getVersionString(), result.getDownloadUrl()))
				).submit(this);*/

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
			SpongeNegativityPlayer nPlayer = SpongeNegativityPlayer.getNegativityPlayer(player);
			nPlayer.saveData();
		}
		if (!ProxyCompanionManager.isIntegrationEnabled())
			Task.builder().async().delayTicks(1).execute(new Runnable() {
				@Override
				public void run() {
					Stats.updateStats(StatsType.ONLINE, 0 + "");
				}
			}).submit(this);
		Database.close();
	}

	@Listener
	public void onGameStart(GameStartingServerEvent e) {
		loadItemBypasses();
		try {
			Class.forName("eu.crushedpixel.sponge.packetgate.api.registry.PacketGate");
			hasPacketGate = true;
			PacketGateManager.check();
		} catch (ClassNotFoundException e1) {
			hasPacketGate = false;
			Logger log = getLogger();
			log.warn("----- Negativity Problem -----");
			log.warn("");
			log.warn("Error while loading PacketGate. Plugin not found.");
			log.warn("Please download it available here: https://github.com/CrushedPixel/PacketGate/releases");
			log.warn("Then, put it in the mods folder.");
			log.warn("Restart your server and now, it will be working");
			log.warn("");
			log.warn("----- Negativity Problem -----");
		}
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
		
		viaVersionSupport = Sponge.getPluginManager().isLoaded("viaversion");

		loadCommands(false);

		channel = Sponge.getChannelRegistrar().createRawChannel(this, NegativityMessagesManager.CHANNEL_ID);
		channel.addListener(new ProxyCompanionListener());
		if (Sponge.getChannelRegistrar().isChannelAvailable("FML|HS")) {
			fmlChannel = Sponge.getChannelRegistrar().getOrCreateRaw(this, "FML|HS");
			fmlChannel.addListener(new FmlRawDataListener());
		}
		AbstractInventory.init(this);
	}

	public void reloadCommands() {
		loadCommands(true);
	}

	private void loadCommands(boolean reload) {
		CommandManager cmd = Sponge.getCommandManager();

		if (!reload) {
			cmd.register(this, NegativityCommand.create(), "negativity");
			cmd.register(this, MigrateOldBansCommand.create(), "negativitymigrateoldbans");
			cmd.register(this, ModCommand.create(), "mod");
			cmd.register(this, KickCommand.create(), "nkick");
			cmd.register(this, LangCommand.create(), "nlang");
		}

		reloadCommand("report_command", cmd, ReportCommand::create, "report", "repot");
		reloadCommand("ban_command", cmd, BanCommand::create, "nban", "negban");
		reloadCommand("unban_command", cmd, UnbanCommand::create, "nunban", "negunban");
	}

	private void reloadCommand(String configKey, CommandManager manager, Supplier<CommandCallable> command, String... aliases) {
		reloadCommand(configKey, config.getBoolean(configKey), manager, command, aliases);
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
		Ban activeBan = BanManager.getActiveBan(playerId);
		if (activeBan != null) {
			NegativityAccount account = NegativityAccount.get(playerId);
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
		if (UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(Text.builder("Ce serveur utilise Negativity ! Waw :')").color(TextColors.GREEN).build());
		SpongeNegativityPlayer.removeFromCache(p.getUniqueId());
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.TIME_INVINCIBILITY = System.currentTimeMillis() + 8000;
		Task.builder().delayTicks(20).execute(new Runnable() {
			@Override
			public void run() {
				np.initFmlMods();
			}
		}).submit(this);

		if (!ProxyCompanionManager.searchedCompanion) {
			ProxyCompanionManager.searchedCompanion = true;
			Task.builder().delayTicks(20).execute(() -> sendProxyPing(p)).submit(this);
		}

		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
			if (ReportCommand.REPORT_LAST.size() > 0) {
				for (Text msg : ReportCommand.REPORT_LAST)
					p.sendMessage(msg);
				ReportCommand.REPORT_LAST.clear();
			}
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

			Task.builder().async().name("negativity-update-checker-" + p.getName()).execute(() -> {
				if (!SpongeUpdateChecker.ifUpdateAvailable()) {
					return;
				}
				URL downloadUrl;
				try {
					downloadUrl = new URL(SpongeUpdateChecker.getDownloadUrl());
				} catch (MalformedURLException ex) {
					getLogger().error("Unable to create update download URL", ex);
					return;
				}
				p.sendMessage(Text
						.builder("New version available (" + SpongeUpdateChecker.getVersionString() + "). Download it here.")
						.color(TextColors.YELLOW)
						.onHover(TextActions.showText(Text.of("Click here")))
						.onClick(TextActions.openUrl(downloadUrl))
						.build());
			}).submit(this);
		}
		manageAutoVerif(p);
	}

	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e, @First Player p) {
		Task.builder().delayTicks(5).execute(() -> {
			SpongeNegativityPlayer.removeFromCache(p);
			NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
			UUID playerId = p.getUniqueId();
			accountManager.save(playerId);
			accountManager.dispose(playerId);
		}).submit(this);
	}

	@Listener
	public void onMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (np.isFreeze && !p.getLocation().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.AIR))
			e.setCancelled(true);
	}

	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Player p) {
		String blockId = e.getTransactions().get(0).getOriginal().getState().getType().getId();
		NegativityAccount.get(p.getUniqueId()).getMinerate().addMine(MinerateType.fromId(blockId), p);
	}

	public void loadConfig() {
		log = config.getBoolean("log_alerts");
		log_console = config.getBoolean("log_alerts_in_console");
		ProxyCompanionManager.updateForceDisabled(config.getBoolean("disableProxyIntegration"));
		hasBypass = config.getBoolean("Permissions.bypass.active");
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

	public static void manageAutoVerif(Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
				if (c.isAutoVerif()) {
					np.startAnalyze(c);
					if (c.needPacket())
						needPacket = true;
				}
			}
		if (needPacket)
			SpongeNegativityPlayer.INJECTED.add(p);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof) {
		return alertMod(type, p, c, reliability, proof, "", 1);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
							   String hover_proof) {
		return alertMod(type, p, c, reliability, proof, hover_proof, 1);
	}

	@Deprecated
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
								   String hover_proof, String stats_send) {
		return alertMod(type, p, c, reliability, proof, hover_proof, 1);
	}
	

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
								   String hover_proof, int alertCounts) {
		if(!c.isActive())
			return false;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (c.equals(Cheat.forKey(CheatKeys.BLINK)))
			if (!np.already_blink) {
				np.already_blink = true;
				return false;
			}
		if (np.isInFight && c.isBlockedInFight())
			return false;
		if (p.getItemInHand(HandTypes.MAIN_HAND).isPresent())
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand(HandTypes.MAIN_HAND).get().getType().getId()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand(HandTypes.MAIN_HAND).get().getType().getId()).getWhen()
						.equals(WhenBypass.ALWAYS))
					return false;
		Optional<BlockRayHit<World>> target = BlockRay.from(p).skipFilter(BlockRay.onlyAirFilter()).stopFilter(BlockRay.onlyAirFilter()).distanceLimit(7).build().end();
		if(target.isPresent() && !target.get().getLocation().getBlock().getType().equals(BlockTypes.AIR))
			if (ItemUseBypass.ITEM_BYPASS.containsKey(target.get().getLocation().getBlock().getType().getId()))
				if (ItemUseBypass.ITEM_BYPASS.get(target.get().getLocation().getBlock().getType().getId()).getWhen().equals(WhenBypass.LOOKING))
					return false;

		int ping = Utils.getPing(p);
		long timeMillis = System.currentTimeMillis();
		if (np.TIME_INVINCIBILITY > timeMillis || reliability < 30 || ping > c.getMaxAlertPing()
				|| p.getHealthData().get(Keys.HEALTH).get() == 0.0D
				|| Adapter.getAdapter().getConfig().getInt("tps_alert_stop") > Utils.getLastTPS() || ping < 0
				|| np.isFreeze)
			return false;
		Sponge.getEventManager().post(new PlayerCheatEvent(type, p, c, reliability, hover_proof, ping));
		if (hasBypass && (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "bypass.all") ||
				Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().toLowerCase()))) {
			PlayerCheatEvent.Bypass bypassEvent = new PlayerCheatEvent.Bypass(type, p, c, reliability, hover_proof, ping);
			Sponge.getEventManager().post(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		PlayerCheatEvent.Alert alert = new PlayerCheatEvent.Alert(type, p, c, reliability, c.getReliabilityAlert() < reliability,
						ping, proof, hover_proof, alertCounts);
		Sponge.getEventManager().post(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		np.addWarn(c);
		logProof(type, p, c, reliability, proof, ping);
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatEvent.Kick kick = new PlayerCheatEvent.Kick(type, p, c, reliability, hover_proof, ping);
			Sponge.getEventManager().post(kick);
			if (!kick.isCancelled())
				p.kick(Messages.getMessage(p, "kick.neg_kick", "%cheat%", c.getName()));
		}
		if(np.isBanned()) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}

		if (BanUtils.banIfNeeded(np, c, reliability) != null) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}
		if(timeBetweenAlert != -1) {
			List<PlayerCheatEvent.Alert> tempList = np.pendingAlerts.containsKey(c) ? np.pendingAlerts.get(c) : new ArrayList<>();
			tempList.add(alert);
			np.pendingAlerts.put(c, tempList);
			return true;
		}

		sendAlertMessage(np, alert);
		return true;
	}

	@Deprecated
	public static void sendAlertMessage(ReportType type, Player p, Cheat c, int reliability,
										String hoverProof, SpongeNegativityPlayer np, int ping, PlayerCheatEvent.Alert alert, int alertsCount, String stats_send) {
		sendAlertMessage(type, p, c, reliability, hoverProof, np, ping, alert, alertsCount);
	}

	@Deprecated
	public static void sendAlertMessage(ReportType type, Player p, Cheat c, int reliability,
										String hoverProof, SpongeNegativityPlayer np, int ping, PlayerCheatEvent.Alert alert, int alertsCount) {
		sendAlertMessage(np, alert);
	}

	public static void sendAlertMessage(SpongeNegativityPlayer np, PlayerCheatEvent.Alert alert) {
		Cheat c = alert.getCheat();
		int reliability = alert.getReliability();
		if(reliability == 0) {// alert already sent
			np.pendingAlerts.remove(c);
			return;
		}
		Player p = alert.getTargetEntity();
		int ping = alert.getPing();
		if(alert.getNbAlertConsole() > 0 && log_console) {
				INSTANCE.getLogger().info("New " + alert.getReportType().getName() + " for " + p.getName()
						+ " (UUID: " + p.getUniqueId().toString() + ") (ping: " + ping + ") : suspected of cheating ("
						+ c.getName() + ") " + (alert.getNbAlertConsole() > 1 ? alert.getNbAlertConsole() + " times " : "") + "Reliability: " + reliability);
		}
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			sendAlertMessage(p, c.getName(), reliability, ping, alert.getHoverProof(), alert.getNbAlert());
			np.pendingAlerts.remove(c);
		} else {
			String hover_proof = alert.getHoverProof();
			boolean hasPermPeople = false;
			for (Player pl : Utils.getOnlinePlayers()) {
				SpongeNegativityPlayer npMod = SpongeNegativityPlayer.getNegativityPlayer(pl);
				if (!Perm.hasPerm(npMod, Perm.SHOW_ALERT) || npMod.disableShowingAlert) {
					continue;
				}

				pl.sendMessage(createAlertText(p, c, hover_proof, ping, alert.getNbAlert(), alert.getAlertMessageKey(), reliability, pl));

				hasPermPeople = true;
			}
			if(hasPermPeople) {
				np.pendingAlerts.remove(c);
				Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			}
		}
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
								"%ping%", String.valueOf(ping))
								+ (hoverProof.isEmpty() ? "" : "\n" + hoverProof))))
				.build();
	}

	private static void logProof(ReportType type, Player p, Cheat c, int reliability, String proof, int ping) {
		if (!log)
			return;
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.logProof(stamp + ": (" + ping + "ms) " + reliability + "% " + c.getKey() + " > " + proof + ". " + (viaVersionSupport ? "Player version: " + np.getPlayerVersion().name() + " " : "") + ". TPS: " + Utils.getLastTPS());
	}

	public Path getDataFolder() {
		return configDir;
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}

	private static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, String hover, int alertsCount) {
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
			HashMap<String, String> playerMods = SpongeNegativityPlayer.getNegativityPlayer(player).MODS;
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
