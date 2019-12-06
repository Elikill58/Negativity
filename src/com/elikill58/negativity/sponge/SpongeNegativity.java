package com.elikill58.negativity.sponge;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
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
import org.spongepowered.api.text.format.TextColors;

import com.elikill58.negativity.sponge.commands.BanCommand;
import com.elikill58.negativity.sponge.commands.LangCommand;
import com.elikill58.negativity.sponge.commands.ModCommand;
import com.elikill58.negativity.sponge.commands.NegativityCommand;
import com.elikill58.negativity.sponge.commands.ReportCommand;
import com.elikill58.negativity.sponge.commands.SuspectCommand;
import com.elikill58.negativity.sponge.commands.UnbanCommand;
import com.elikill58.negativity.sponge.listeners.FightManager;
import com.elikill58.negativity.sponge.listeners.InventoryClickManagerEvent;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;
import com.elikill58.negativity.sponge.timers.ActualizerTimer;
import com.elikill58.negativity.sponge.timers.PacketsTimers;
import com.elikill58.negativity.sponge.timers.PendingAlertsTimer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpongeAdapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

@Plugin(id = "negativity", name = "Negativity", version = "1.4", description = "It's an Advanced AntiCheat Detection", authors = "Elikill58", dependencies = {
		@Dependency(id = "packetgate") })
public class SpongeNegativity implements RawDataListener {

	public static SpongeNegativity INSTANCE;

	@Inject
	private PluginContainer plugin;
	@Inject
	public Logger logger;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private ConfigurationNode config;
	private HoconConfigurationLoader configLoader;
	public static RawDataChannel channel = null, fmlChannel = null;

	public static final List<PlayerCheatEvent.Alert> ALERTS = new ArrayList<>();
	public static final Map<UUID, Map<Cheat, Long>> LAST_ALERTS_TIME = new HashMap<>();

	public PluginContainer getContainer() {
		return plugin;
	}

	public static boolean log = true, isOnBungeecord = false, hasPacketGate = false, hasPrecogs = false,
			hasBypass = true;

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		INSTANCE = this;

		loadConfig();
		Adapter.setAdapter(new SpongeAdapter(this));
		UniversalUtils.init();
		Cheat.loadCheat();
		EventManager eventManager = Sponge.getEventManager();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			if(c.hasListener())
				eventManager.registerListeners(this, c);
		}
		eventManager.registerListeners(this, new InventoryClickManagerEvent());
		eventManager.registerListeners(this, new FightManager());
		Task.builder().execute(new PacketsTimers()).delayTicks(0).interval(1, TimeUnit.SECONDS)
				.name("negativity-packets").submit(this);
		Task.builder().execute(new ActualizerTimer()).interval(1, TimeUnit.SECONDS)
				.name("negativity-actualizer").submit(this);
		Task.builder().execute(new PendingAlertsTimer()).interval(1, TimeUnit.SECONDS)
				.name("negativity-pending-alerts").submit(this);
		plugin.getLogger().info("Negativity v" + plugin.getVersion().get() + " loaded.");

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

		if (!isOnBungeecord)
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
		Adapter.getAdapter().loadLang();
	}

	@Listener
	public void onGameStop(GameStoppingServerEvent e) {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			SpongeNegativityPlayer nPlayer = SpongeNegativityPlayer.getNegativityPlayer(player);
			nPlayer.saveData();
		}
		if (!isOnBungeecord)
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
		CommandManager cmd = Sponge.getCommandManager();

		cmd.register(this, NegativityCommand.create(), "negativity");
		cmd.register(this, ModCommand.create(), "mod");
		cmd.register(this, LangCommand.create(), "nlang");

		if (config.getNode("report_command").getBoolean()) {
			cmd.register(this, ReportCommand.create(), "report");
		}

		if (config.getNode("ban_command").getBoolean()) {
			cmd.register(this, BanCommand.create(), "nban", "negban");
		}

		if (config.getNode("unban_command").getBoolean()) {
			cmd.register(this, UnbanCommand.create(), "nunban", "negunban");
		}

		if (SuspectManager.ENABLED_CMD) {
			cmd.register(this, SuspectCommand.create(), "suspect");
		}

		channel = Sponge.getChannelRegistrar().createRawChannel(this, UniversalUtils.CHANNEL_NEGATIVITY);
		if (Sponge.getChannelRegistrar().isChannelAvailable("FML|HS")) {
			fmlChannel = Sponge.getChannelRegistrar().getOrCreateRaw(this, "FML|HS");
			fmlChannel.addListener(this);
		}
	}

	@Listener
	public void onLogin(ClientConnectionEvent.Login e) {
		UUID playerId = e.getTargetUser().getUniqueId();
		SpongeNegativityPlayer.removeFromCache(playerId, false);

		NegativityAccount userAccount = Adapter.getAdapter().getNegativityAccount(playerId);
		if (Ban.isBanned(userAccount)) {
			if (Ban.canConnect(userAccount))
				return;
			e.setCancelled(true);
			e.setMessage(Messages.getMessage(userAccount, "ban.kick_" + (userAccount.isBanDef() ? "def" : "time"), "%reason%",
					userAccount.getBanReason(), "%time%", (userAccount.getBanTime()), "%by%", userAccount.getBanBy()));
		}
	}

	@Listener
	public void onJoin(ClientConnectionEvent.Join e, @First Player p) {
		if (UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(Text.builder("Ce serveur utilise Negativity ! Waw :')").color(TextColors.GREEN).build());
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.TIME_INVINCIBILITY = System.currentTimeMillis() + 8000;
		Task.builder().delayTicks(20).execute(new Runnable() {
			@Override
			public void run() {
				np.initFmlMods();
			}
		}).submit(this);
		if (Perm.hasPerm(np, "showAlert")) {
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
			Adapter.getAdapter().invalidateAccount(p.getUniqueId());
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
		SpongeNegativityPlayer.getNegativityPlayer(p).mineRate.addMine(MinerateType.getMinerateType(
				e.getTransactions().get(0).getOriginal().getLocation().get().getBlock().getType().getId()));
	}

	private void loadConfig() {
		try {
			File configFile = new File(configDir.toFile(), "config.conf");
			if (!configFile.exists()) {
				Sponge.getAssetManager().getAsset(this, "config.conf").ifPresent((configAsset) -> {
					try {
						configAsset.copyToDirectory(INSTANCE.configDir);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
			config = (configLoader = HoconConfigurationLoader.builder().setFile(configFile).build()).load();
			log = config.getNode("log_alerts").getBoolean();
			isOnBungeecord = config.getNode("hasBungeecord").getBoolean();
			hasBypass = config.getNode("Permissions").getNode("bypass").getNode("active").getBoolean();
			for (ConfigurationNode cn : config.getNode("items").getChildrenList())
				new ItemUseBypass(cn.getKey().toString(), cn.getNode("cheats").getString(),
						cn.getNode("when").getString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConfigurationNode getConfig() {
		return INSTANCE.config;
	}

	public static void saveConfig() {
		try {
			INSTANCE.configLoader.save(INSTANCE.config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}

	public static void manageAutoVerif(Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive() || Cheat.ALL.isActive()) {
				if (c.isAutoVerif() || Cheat.ALL.isAutoVerif()) {
					np.startAnalyze(c);
					if (c.needPacket() || Cheat.ALL.needPacket())
						needPacket = true;
				}
			}
		if (needPacket)
			SpongeNegativityPlayer.INJECTED.add(p);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof) {
		return alertMod(type, p, c, reliability, proof, "");
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			String hover_proof) {
		if(!c.isActive())
			return false;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (c.equals(Cheat.fromString("BLINK").get()))
			if (!np.already_blink) {
				np.already_blink = true;
				return false;
			}
		if (np.isInFight && c.isBlockedInFight())
			return false;
		if (p.getItemInHand(HandTypes.MAIN_HAND).isPresent())
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand(HandTypes.MAIN_HAND).get().getType()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand(HandTypes.MAIN_HAND).get().getType()).getWhen()
						.equals(WhenBypass.ALWAYS))
					return false;
		int ping = Utils.getPing(p);
		long timeMillis = System.currentTimeMillis();
		if (np.TIME_INVINCIBILITY > timeMillis || reliability < 30 || ping > c.getMaxAlertPing()
				|| p.getHealthData().get(Keys.HEALTH).get() == 0.0D
				|| getInstance().config.getNode("tps_alert_stop").getInt() > Utils.getLastTPS() || ping < 0
				|| np.isFreeze)
			return false;
		Sponge.getEventManager().post(new PlayerCheatEvent(type, p, c, reliability, hover_proof, ping));
		if (hasBypass && Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p),
				"Permissions.bypass." + c.getKey().toLowerCase())) {
			PlayerCheatEvent.Bypass bypassEvent = new PlayerCheatEvent.Bypass(type, p, c, reliability, hover_proof, ping);
			Sponge.getEventManager().post(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		logProof(type, p, c, reliability, proof, ping);
		PlayerCheatEvent.Alert alert = new PlayerCheatEvent.Alert(type, p, c, reliability,
				c.getReliabilityAlert() < reliability, hover_proof, ping);
		Sponge.getEventManager().post(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		np.addWarn(c);
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatEvent.Kick kick = new PlayerCheatEvent.Kick(type, p, c, reliability, hover_proof, ping);
			Sponge.getEventManager().post(kick);
			if (!kick.isCancelled())
				p.kick(Messages.getMessage(p, "kick", "%cheat%", c.getName()));
		}
		if(np.isBanned())
			return false;
		Ban.manageBan(c, np, reliability);
		if (Ban.isBanned(np.getAccount()))
			return false;

		int timeBetweenTwoAlerts = Adapter.getAdapter().getIntegerInConfig("time_between_alert");
		if (timeBetweenTwoAlerts >= 0) {
			Map<Cheat, Long> lastAlerts = LAST_ALERTS_TIME.computeIfAbsent(p.getUniqueId(), playerId -> new HashMap<>());
			Long lastAlert = lastAlerts.put(c, timeMillis);
			if (lastAlert != null && (timeMillis - lastAlert) < timeBetweenTwoAlerts) {
				np.pendingAlerts.computeIfAbsent(c, cheat -> new ArrayList<>())
						.add(alert);
				return true;
			}
		}

		sendAlertMessage(type, p, c, reliability, hover_proof, np, ping, alert, false);
		np.pendingAlerts.remove(c);
		return true;
	}

	public static void sendAlertMessage(ReportType type, Player p, Cheat c, int reliability,
										String hoverProof, SpongeNegativityPlayer np, int ping, PlayerCheatEvent.Alert alert, boolean isMultiple) {
		if (isOnBungeecord) {
			sendMessage(p, c.getName(), reliability, ping, hoverProof, isMultiple);
			return;
		}

		if (log) {
			INSTANCE.getLogger().info("New {} for {} (UUID: {})  (ping: {}) : suspected of cheating ({}) Reliability: {}",
					type.getName(), p.getName(), p.getUniqueId().toString(), ping, c.getName(), reliability);
		}

		List<PlayerCheatEvent.Alert> pendingAlerts = np.pendingAlerts.get(c);
		int pendingAlertsCount = pendingAlerts != null ? pendingAlerts.size() : 0;

		String messageKey = "negativity.alert";
		int messageReliability = reliability;
		if (pendingAlertsCount > 1) {
			messageKey = "negativity.alert_multiple";
			messageReliability = 100;
		}

		boolean alertSent = false;
		for (Player pl : Utils.getOnlinePlayers()) {
			if (!Perm.hasPerm(np, "showAlert")) {
				continue;
			}

			pl.sendMessage(createAlertText(p, c, hoverProof, ping, pendingAlertsCount, messageKey, messageReliability, pl));

			alertSent = true;
		}

		if (!alertSent) {
			ALERTS.add(alert);
		}
	}

	public static Text createAlertText(Player suspect, Cheat cheat, String hoverProof, int ping, int pendingAlertsCount,
									   String messageKey, int reliability, Player receiver) {
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
		SpongeNegativityPlayer.getNegativityPlayer(p).logProof(
				stamp + ": (" + ping + "ms) " + reliability + "% " + c.getKey() + " > " + proof);
	}

	public Path getDataFolder() {
		return configDir;
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}

	private static void sendMessage(Player p, String cheatName, int reliability, int ping, String hover, boolean isMultiple) {
		String msg = p.getName() + "/**/" + cheatName + "/**/" + reliability + "/**/" + ping + "/**/" + hover + "/**/" + (isMultiple ? "alert_multiple" : "alert");
		channel.sendTo(p, (payload) -> {
			payload.writeUTF(msg);
		});
	}

	public static void sendReportMessage(Player p, String reportMsg, String nameReported) {
		channel.sendTo(p, (payload) -> {
			payload.writeUTF(nameReported + "/**/" + reportMsg + "/**/" + p.getName());
		});
	}

	@Override
	public void handlePayload(ChannelBuf channelBuf, RemoteConnection connection, Type side) {
		try {
			if (!(connection instanceof PlayerConnection))
				return;
			SpongeNegativityPlayer.getNegativityPlayer(((PlayerConnection) connection).getPlayer()).MODS
					.putAll(Utils.getModsNameVersionFromMessage(
							new String(channelBuf.readBytes(channelBuf.available()), StandardCharsets.UTF_8)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
