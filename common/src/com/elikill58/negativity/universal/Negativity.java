package com.elikill58.negativity.universal;

import static com.elikill58.negativity.universal.verif.VerificationManager.hasVerifications;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
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
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.alerts.hook.AmountAlertSender;
import com.elikill58.negativity.universal.alerts.hook.InstantAlertSender;
import com.elikill58.negativity.universal.alerts.hook.TimeAlertSender;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.file.FileSaverTimer;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.proxysender.ProxySenderManager;
import com.elikill58.negativity.universal.report.ReportType;
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
	private static AlertSender alertSender;

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
			CheatHover hover, int amount) {
		if(!c.isActive() || reliability < 55 || tpsDrop || amount == 0)
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
		np.addWarn(c, reliability, amount);
		logProof(np, type, p, c, reliability, checkName, proof, ping, amount);
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			EventManager.callEvent(kick);
			if (!kick.isCancelled())
				p.kick(Messages.getMessage(p, "kick.neg_kick", "%cheat%", c.getName(), "%reason%", np.getReason(c), "%playername%", p.getName()));
		}
		if(BanManager.isBanned(np.getUUID())) {
			Stats.updateStats(StatsType.CHEAT, c.getKey().getKey(), reliability + "");
			return false;
		}

		if (BanUtils.banIfNeeded(np, c, reliability).isSuccess()) {
			Stats.updateStats(StatsType.CHEAT, c.getKey().getKey(), reliability + "");
			return false;
		}
		manageAlertCommand(np, type, p, c, reliability);
		alertSender.alert(np, alert);
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
		Configuration conf = Adapter.getAdapter().getConfig();
		if(!conf.getBoolean("alert.command.active") || conf.getInt("alert.command.reliability_need") > reliability)
			return;
		for(String s : conf.getStringList("alert.command.run")) {
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
			np.ALERT_NOT_SHOWED.remove(c.getKey());
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
			ProxySenderManager.sendAlertMessage(p, c.getName(), reliability, ping, hoverMsg, alert.getNbAlert());
			np.ALERT_NOT_SHOWED.remove(c.getKey());
		} else {
			boolean hasPermPeople = false;
			for (Player pl : ada.getOnlinePlayers()) {
				NegativityPlayer npMod = NegativityPlayer.getNegativityPlayer(pl);
				boolean basicPerm = Perm.hasPerm(npMod, Perm.SHOW_ALERT);
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(pl, npMod, basicPerm);
				EventManager.callEvent(permissionEvent);
				if (permissionEvent.isCancelled() || npMod.disableShowingAlert)
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
				np.ALERT_NOT_SHOWED.remove(c.getKey());
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

	private static void logProof(NegativityPlayer np, ReportType type, Player p, Cheat c, int reliability,
			String checkName, String proof, int ping, int amount) {
		if(!log || type.equals(ReportType.INFO))
			return;
		String time = new Timestamp(System.currentTimeMillis()).toString().split("\\.")[0];
		np.logProof(time + ": (" + ping + "ms) " + reliability + "% " + c.getKey() + " x" + amount + " - " + checkName
				+ " > " + proof + " | Warn: " + np.getWarn(c) + ", Version: " + p.getPlayerVersion().name() + ". TPS: " + getVisualTPS());
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
		
		integratedPlugins.clear();

		DefaultConfigValue.init();
		Database.init();
		Perm.init();
		BanManager.init();
		TranslatedMessages.init();
		NegativityAccountStorage.init();
		EventManager.load();
		if(!ada.getPlatformID().isProxy()) {
			Special.loadSpecial();
			Cheat.loadCheat();
			BypassManager.loadBypass();
			BedrockPlayerManager.init();
			PlayerVersionManager.init();
			PlayerModificationsManager.init();
			VerificationManager.init();
			WebhookManager.init();
			ada.registerNewIncomingChannel(ada.getServerVersion().isNewerOrEquals(Version.V1_13) ? "minecraft:brand" : "MC|Brand", (p, msg) -> {
				NegativityPlayer.getNegativityPlayer(p).setClientName(new String(msg).substring(1));
			});
			initAlertShower(ada);
			FileSaverTimer old = FileSaverTimer.getInstance();
			if(old != null)
				old.runAll();
			else
				ada.getScheduler().runRepeatingAsync(new FileSaverTimer(), 20);
		}
		UniversalUtils.init();
		
		Configuration config = ada.getConfig();
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
					&& !adapter.getPlatformID().equals(((PlatformDependentExtension) extension).getPlatform())) {
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
	
	public static AlertSender getAlertShower() {
		return alertSender;
	}
	
	public static AlertSender getAlertShowerOfTypeName(String type) {
		if(type.equalsIgnoreCase("instant")) {
			return new InstantAlertSender();
		} else if(type.equalsIgnoreCase("amount")) {
			return new AmountAlertSender();
		} else { // default one
			return new TimeAlertSender();
		}
	}
	
	private static void initAlertShower(Adapter ada) {
		Configuration config = ada.getConfig().getSection("alert.show");

		String type = config.getString("type", "time");
		alertSender = getAlertShowerOfTypeName(type);
		alertSender.config(config);
	}
	
	public static void refreshAlertShower(Adapter ada, AlertSender newShower) {
		if(alertSender != null)
			alertSender.stop();
		Configuration config = ada.getConfig().getSection("alert.show");

		alertSender = newShower;
		alertSender.config(config);
	}
	
	public static void setAlertShower(String type) {
		setAlertShower(getAlertShowerOfTypeName(type));
	}
	
	public static void setAlertShower(AlertSender shower) {
		Adapter ada = Adapter.getAdapter();
		Configuration config = ada.getConfig();
		config.set("alert.show.type", shower.getName());
		config.set("alert.show.value", shower.getValue());
		config.save();
		refreshAlertShower(ada, shower);
	}
}
