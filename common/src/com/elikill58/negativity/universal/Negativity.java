package com.elikill58.negativity.universal;

import static com.elikill58.negativity.universal.verif.VerificationManager.hasVerifications;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatBypassEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatKickEvent;
import com.elikill58.negativity.api.events.negativity.ShowAlertPermissionEvent;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.common.timers.ActualizeInvTimer;
import com.elikill58.negativity.common.timers.AnalyzePacketTimer;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.file.FileSaverTimer;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.storage.proof.NegativityProofStorage;
import com.elikill58.negativity.universal.utils.SemVer;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerificationManager;
import com.elikill58.negativity.universal.webhooks.WebhookManager;
import com.elikill58.negativity.universal.webhooks.messages.AlertWebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class Negativity {

	public static boolean log = false;
	public static boolean log_console = false;
	public static boolean hasBypass = false;
	public static boolean tpsDrop = false;
	
	private static ScheduledTask actualizeInvTimer, analyzePacketTimer;

	/**
	 * Try to alert moderator.
	 * It will not check everything, but there is some verification which are made such as:
	 * - if the cheat is active
	 * - the reliability amount
	 * - Detection disabled on verification
	 * - Bypass with permissions
	 * - ...
	 * 
	 * @param type the report type
	 * @param p the player which have to be reported
	 * @param c the cheat that just detect the player
	 * @param reliability the reliability of the cheat
	 * @param checkName the name of the check
	 * @param proof the proof which will be on proof file
	 * @return true if the player have to be set back
	 */
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof) {
		return alertMod(type, p, c, reliability, checkName, proof, null, 1);
	}

	/**
	 * Try to alert moderator.
	 * It will not check everything, but there is some verification which are made such as:
	 * - if the cheat is active
	 * - the reliability amount
	 * - Detection disabled on verification
	 * - Bypass with permissions
	 * - ...
	 * 
	 * @param type the report type
	 * @param p the player which have to be reported
	 * @param c the cheat that just detect the player
	 * @param reliability the reliability of the cheat
	 * @param checkName the name of the check
	 * @param proof the proof which will be on proof file
	 * @param hover the cheatHover see in alert
	 * @return true if the player have to be set back
	 */
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof,
			CheatHover hover) {
		return alertMod(type, p, c, reliability, checkName, proof, hover, 1);
	}

	/**
	 * Try to alert moderator.
	 * It will not check everything, but there is some verification which are made such as:
	 * - if the cheat is active
	 * - the reliability amount
	 * - Detection disabled on verification
	 * - Bypass with permissions
	 * - ...
	 * 
	 * @param type the report type
	 * @param p the player which have to be reported
	 * @param c the cheat that just detect the player
	 * @param reliability the reliability of the cheat
	 * @param checkName the name of the check
	 * @param proof the proof which will be on proof file
	 * @param hover the cheatHover see in alert
	 * @param amount the amount of alert
	 * @return true if the player have to be set back
	 */
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof,
			CheatHover hover, long amount) {
		if(!c.isActive() || reliability < 55 || tpsDrop || amount <= 0)
			return false;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.already_blink && c.getKey().equals(CheatKeys.BLINK)) {
			np.already_blink = true;
			return false;
		}
		if(VerificationManager.isDisablingAlertOnVerif() && hasVerifications(p.getUniqueId()))
			return false;
		int ping = p.getPing();
		if (ping < 0)
			return false;
		
		EventManager.callEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().getLowerKey())
				|| Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.BYPASS_ALL))) {
			PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, c, reliability);
			EventManager.callEvent(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(type, p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover, amount);
		EventManager.callEvent(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		long oldWarn = np.addWarn(c, reliability, amount);
		if(oldWarn == -1) // no warn added
			return false;
		if(log && !type.equals(ReportType.INFO))
			NegativityProofStorage.getStorage().saveProof(new Proof(np, type, c.getKey(), checkName, ping, amount, reliability, proof));
		if (c.allowKick() && ((long) (oldWarn / c.getAlertToKick())) < ((long) (np.getWarn(c) / c.getAlertToKick()))) { // if reach new alert state
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			EventManager.callEvent(kick);
			if (!kick.isCancelled()) {
				if(Adapter.getAdapter().getConfig().getBoolean("log_alert_with_kick", false)) { // if should log
					manageAlertCommand(np, type, p, c, reliability);
					sendAlertMessage(np, alert);
					// don't run set back options because player will be offline
				}
				p.kick(Messages.getMessage(p, "kick.neg_kick", "%cheat%", c.getName(), "%reason%", np.getReason(c), "%playername%", p.getName()));
				return false;
			}
		}
		if(BanManager.isBanned(np.getUUID())) {
			Stats.updateStats(StatsType.CHEAT, c.getKey().getKey(), reliability + "");
			return false;
		}

		if (BanManager.autoBan && BanUtils.banIfNeeded(np, c, reliability, oldWarn).isSuccess()) {
			Stats.updateStats(StatsType.CHEAT, c.getKey().getKey(), reliability + "");
			return false;
		}
		manageAlertCommand(np, type, p, c, reliability);
		AlertSender.getAlertShower().alert(np, alert);
		if(c.isSetBack())
			c.performSetBack(p);
		return true;
	}

	/**
	 * Run all command set in config is it's active and the reliability is enough
	 * 
	 * @param np the negativity player which create alert
	 * @param type the type of the alert
	 * @param p the player which create alert
	 * @param c the cheat which detect player
	 * @param reliability the reliability of detection
	 */
	private static void manageAlertCommand(NegativityPlayer np, ReportType type, Player p, Cheat c, int reliability) {
		Configuration conf = Adapter.getAdapter().getConfig().getSection("alert.command");
		if(conf == null || !conf.getBoolean("active") || conf.getInt("reliability_need") > reliability)
			return;
		int cooldown = conf.getInt("cooldown", 0);
		if(cooldown > 0) {
			if(np.longs.get(CheatKeys.ALL, "alert-cmd-cooldown", 0l) > System.currentTimeMillis())
				return; // has cooldown
			np.longs.set(CheatKeys.ALL, "alert-cmd-cooldown", System.currentTimeMillis() + cooldown);
		}
		for(String s : conf.getStringList("run")) {
			Adapter.getAdapter().runConsoleCommand(UniversalUtils.replacePlaceholders(s, "%name%",
					p.getName(), "%uuid%", p.getUniqueId().toString(), "%cheat_key%", c.getKey().getLowerKey(), "%cheat_name%",
					c.getName(), "%reliability%", reliability, "%report_type%", type.name(), "%warn%", np.getWarn(c)));
		}
	}

	/**
	 * Send alert message to all player which can receive them
	 * If any, the message will keep in cache and showed when someone can see them
	 * If a lot of one, all message are compiled into one (and amount or are additionned)
	 * 
	 * @param np the negativity player which create alert
	 * @param alert a compiled alert
	 */
	public static void sendAlertMessage(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		Cheat c = alert.getCheat();
		int reliability = alert.getReliability();
		if(reliability == 0) {// alert already sent
			np.alertNotShowed.remove(c.getKey());
			return;
		}
		Adapter ada = Adapter.getAdapter();
		Player p = alert.getPlayer();
		int ping = alert.getPing();
		if(alert.getNbAlertConsole() > 0 && log_console) {
			Location location = p.getLocation();
			String sLoc = "[" + location.getWorld().getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "]";
			ada.getLogger().info("New " + alert.getReportType().getName() + " for " + p.getName() + " (" + ping + " ms, UUID: "
						+ p.getUniqueId() + ") seem to use " + c.getName() + " "
						+ (alert.getNbAlertConsole() > 1 ? alert.getNbAlertConsole() + " times " : "") + "Reliability: " + reliability + " " + sLoc);
		}
		if(!alert.getReportType().equals(ReportType.INFO))
			WebhookManager.addToQueue(new AlertWebhookMessage(WebhookMessageType.ALERT, p, "Negativity", System.currentTimeMillis(), alert.getNbAlert() == 0 ? 1 : alert.getNbAlert(), alert.getReliability(), alert.getCheat()));
		CheatHover hoverMsg = alert.getHover();
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new AlertMessage(p.getUniqueId(), c.getName(), reliability, ping, hoverMsg, alert.getNbAlert()));
			np.alertNotShowed.remove(c.getKey());
		} else {
			boolean hasPermPeople = false;
			for (Player pl : ada.getOnlinePlayers()) {
				NegativityPlayer npMod = NegativityPlayer.getNegativityPlayer(pl);
				boolean basicPerm = Perm.hasPerm(npMod, Perm.SHOW_ALERT);
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(pl, npMod, basicPerm);
				EventManager.callEvent(permissionEvent);
				if (permissionEvent.isCancelled() || !npMod.getAccount().isShowAlert())
					continue;
				if (permissionEvent.hasBasicPerm()) {
					ada.sendMessageRunnableHover(pl, Messages.getMessage(pl, alert.getAlertMessageKey(), "%name%", p.getName(), "%cheat%", c.getName(),
									"%reliability%", reliability, "%nb%", alert.getNbAlert()),
							Messages.getMessage(pl, "negativity.alert_hover", "%reliability%", reliability, "%ping%", ping)
							+ ChatColor.RESET + (hoverMsg == null ? "" : "\n\n" + hoverMsg.compile(npMod)), "/negativity " + p.getName());
					/*new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(pl, alert.getAlertMessageKey(), "%name%", p.getName(), "%cheat%", c.getName(),
									"%reliability%", String.valueOf(reliability), "%nb%", String.valueOf(alert.getNbAlert())),
							Messages.getMessage(pl, "negativity.alert_hover", "%reliability%", reliability, "%ping%", ping)
									+ ChatColor.RESET + (hoverMsg == null ? "" : "\n\n" + hoverMsg.compile(npMod)),
								"/negativity " + p.getName()).sendToPlayer(pl);*/
					hasPermPeople = true;
				}
			}
			if(hasPermPeople) {
				np.alertNotShowed.remove(c.getKey());
				Stats.updateStats(StatsType.CHEAT, c.getKey().getKey(), reliability + "");
			}
		}
	}

	/**
	 * If there is a proxy, and we don't are on proxy platform, the message is sent to them.
	 * 
	 * @param reporter the player which report the other player
	 * @param reason the reason of the report
	 * @param reported the name of the reported player
	 */
	public static void sendReportMessage(Player reporter, String reason, String reported) {
		try {
			ReportMessage reportMessage = new ReportMessage(reported, reason, reporter.getName());
			reporter.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(reportMessage));
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not send report message to the proxy.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get visual TPS to put them on file
	 * 
	 * @return a visual string of tps
	 */
	public static String getVisualTPS() {
		StringJoiner sj = new StringJoiner(" / ");
		for(double d : Adapter.getAdapter().getTPS())
			sj.add(String.format("%.4f", d));
		return sj.toString();
	}
	
	private static final Set<String> integratedPlugins = Collections.synchronizedSet(new HashSet<>());
	
	/**
	 * Load all Negativity's class and content.
	 * Must to be run after setting adapter
	 */
	public static void loadNegativity() {
		Adapter ada = Adapter.getAdapter();
		ada.getLogger().info("Thanks for buying Negativity <3");
		ada.reloadConfig();
		
		integratedPlugins.clear();

		DefaultConfigValue.init();
		Database.init();
		Perm.init();
		BanManager.init();
		TranslatedMessages.init();
		NegativityAccountStorage.init();
		EventManager.load();
		PlayerVersionManager.init();
		Configuration config = ada.getConfig();
		if(!ada.getPlatformID().isProxy()) {
			Special.loadSpecial();
			Cheat.loadCheat();
			BypassManager.loadBypass();
			BedrockPlayerManager.init();
			PlayerModificationsManager.init();
			NegativityProofStorage.init();
			VerificationManager.init();
			WebhookManager.init();
			ada.registerNewIncomingChannel(ada.getServerVersion().isNewerOrEquals(Version.V1_13) ? "minecraft:brand" : "MC|Brand", (p, msg) -> {
				NegativityPlayer.getNegativityPlayer(p).setClientName(new String(msg).substring(1));
			});
			AlertSender.initAlertShower(ada);
			FileSaverTimer old = FileSaverTimer.getInstance();
			if(old != null)
				old.runAll();
			else
				ada.getScheduler().runRepeatingAsync(FileSaverTimer.getInstance(), Duration.ofSeconds(1), Duration.ofSeconds(1), "Negativity FileSaver");
			if(actualizeInvTimer != null)
				actualizeInvTimer.cancel();
			actualizeInvTimer = ada.getScheduler().runRepeating(new ActualizeInvTimer(), 10, 10);
			if(analyzePacketTimer != null)
				analyzePacketTimer.cancel();
			analyzePacketTimer = ada.getScheduler().runRepeating(new AnalyzePacketTimer(), 20, 20);
			if(config.getBoolean("use-proxy-force", false)) {
				ProxyCompanionManager.forceCompanion();
				ada.getLogger().info("Force proxy used without sending searching message.");
			} else {
				List<Player> players = Adapter.getAdapter().getOnlinePlayers();
				if(!players.isEmpty()) {
					ProxyCompanionManager.sendProxyPing(players.get(0));
				}
			}
		}
		UniversalUtils.init();
		log = config.getBoolean("log_alerts", true);
		log_console = config.getBoolean("log_alerts_in_console", true);
		hasBypass = config.getBoolean("Permissions.bypass.active", false);
		
		if (!integratedPlugins.isEmpty()) {
			ada.getLogger().info("Loaded support for " + String.join(", ", integratedPlugins) + ".");
		}

		new Thread(() -> {
			SemVer latestVersion = UniversalUtils.getLatestVersionIfNewer();
			if (latestVersion != null) {
				ada.getLogger().info("New version of Negativity available: " + latestVersion.toFormattedString() + ". Download it here: https://www.spigotmc.org/resources/86874/");
			}
		}).start();
	}
	
	public static <T> void loadExtensions(Class<T> extensionClass, Predicate<T> extensionConsumer) {
		Adapter adapter = Adapter.getAdapter();
		// First load extensions from negativity
		safelyLoadExtensions(extensionClass, Negativity.class.getClassLoader(), extensionConsumer, adapter);
		// Then those from dependent plugins
		for (ExternalPlugin plugin : adapter.getDependentPlugins()) {
			ClassLoader pluginClassLoader = plugin.getDefault().getClass().getClassLoader();
			safelyLoadExtensions(extensionClass, pluginClassLoader, extensionConsumer, adapter);
		}
	}
	
	private static <T> void safelyLoadExtensions(Class<T> extensionClass, ClassLoader classLoader, Predicate<T> extensionConsumer, Adapter adapter) {
		for (T extension : ServiceLoader.load(extensionClass, classLoader)) {
			try {
				if (extension instanceof PlatformDependentExtension
					&& !((PlatformDependentExtension) extension).getPlatforms().contains(adapter.getPlatformID())) {
					continue;
				}
				
				String dependencyPluginId = null;
				if (extension instanceof PluginDependentExtension) {
					PluginDependentExtension depExt = (PluginDependentExtension) extension;
					dependencyPluginId = depExt.getPluginId();
					if (!adapter.hasPlugin(dependencyPluginId) || !depExt.hasPreRequises()) {
						continue;
					}
				}
				
				if (extensionConsumer.test(extension) && dependencyPluginId != null) {
					integratedPlugins.add(dependencyPluginId);
				}
			} catch (Throwable e) {
				Adapter.getAdapter().getLogger().error("Failed to consume extension " + extension);
				e.printStackTrace();
			}
		}
	}
	
	public static void closeNegativity() {
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		NegativityPlayer.getAllNegativityPlayers().forEach(NegativityPlayer::destroy);
	}
}
