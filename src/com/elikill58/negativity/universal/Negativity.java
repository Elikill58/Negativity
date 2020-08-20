package com.elikill58.negativity.universal;

import static com.elikill58.negativity.universal.verif.VerificationManager.hasVerifications;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.elikill58.negativity.api.ChatColor;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatBypassEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatEvent;
import com.elikill58.negativity.api.events.negativity.PlayerCheatKickEvent;
import com.elikill58.negativity.api.events.negativity.ShowAlertPermissionEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.support.AdvancedBanProcessor;
import com.elikill58.negativity.universal.ban.support.LiteBansProcessor;
import com.elikill58.negativity.universal.ban.support.MaxBansProcessor;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.bypass.ItemUseBypass;
import com.elikill58.negativity.universal.bypass.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerificationManager;

public class Negativity {

	public static boolean log = false, log_console = false, hasBypass = false, essentialsSupport = false, floodGateSupport = false,
			worldGuardSupport = false, gadgetMenuSupport = false, viaVersionSupport = false, protocolSupportSupport = false;
	public static int timeBetweenAlert = -1;

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof) {
		return alertMod(type, p, c, reliability, checkName, proof, null, 1);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof,
			CheatHover hover) {
		return alertMod(type, p, c, reliability, checkName, proof, hover, 1);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String checkName, String proof,
			CheatHover hover, int amount) {
		if(!c.isActive() || reliability < 55)
			return false;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.already_blink && c.getKey().equals(CheatKeys.BLINK)) {
			np.already_blink = true;
			return false;
		}
		if(VerificationManager.isDisablingAlertOnVerif() && !hasVerifications(p.getUniqueId()))
			return false;
		int ping = p.getPing();
		if (p.getHealth() == 0.0D || ping < 0)
			return false;

		ItemStack itemInHand = p.getItemInHand();
		Material blockBelow = p.getLocation().clone().sub(0, 1, 0).getBlock().getType();
		// Why a boolean to check if the block is loaded ? It's to prevent bug which can appear with getTargetBlock method
		boolean hasLoadTargetVisual = false;
		List<Block> targetVisual = null;
		for(Entry<String, ItemUseBypass> itemUseBypass : ItemUseBypass.ITEM_BYPASS.entrySet()) {
			String id = itemUseBypass.getKey();
			ItemUseBypass itemBypass = itemUseBypass.getValue();
			if(itemBypass.getWhen().equals(WhenBypass.ALWAYS)) {
				if(itemInHand != null && itemInHand.getType().getId().equalsIgnoreCase(id)) {
					return false;
				}
			} else if(itemBypass.getWhen().equals(WhenBypass.BELOW)) {
				if(blockBelow.getId().equalsIgnoreCase(id)) {
					return false;
				}
			} else if(itemBypass.getWhen().equals(WhenBypass.LOOKING)) {
				if(!hasLoadTargetVisual) {
					targetVisual = p.getTargetBlock(7);
					hasLoadTargetVisual = true;
				}
				if(!targetVisual.isEmpty()) {
					for(Block b : targetVisual)
						if(b.getType().getId().equalsIgnoreCase(id))
							return false;
				}
			}
		}
		
		EventManager.callEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().toLowerCase())
				|| Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), "bypass.all"))) {
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
		logProof(np, type, p, c, reliability, checkName, proof, ping);
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			EventManager.callEvent(kick);
			if (!kick.isCancelled())
				p.kick(Messages.getMessage(p, "kick.neg_kick", "%cheat%", c.getName(), "%reason%", np.getReason(c), "%playername%", p.getName()));
		}
		if(BanManager.isBanned(np.getUUID())) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}

		if (BanUtils.banIfNeeded(np, c, reliability) != null) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}
		manageAlertCommand(type, p, c, reliability);
		if(timeBetweenAlert != -1) {
			List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.containsKey(c) ? np.ALERT_NOT_SHOWED.get(c) : new ArrayList<>();
			tempList.add(alert);
			np.ALERT_NOT_SHOWED.put(c, tempList);
		} else
			sendAlertMessage(np, alert);
		return true;
	}

	private static void manageAlertCommand(ReportType type, Player p, Cheat c, int reliability) {
		Configuration conf = Adapter.getAdapter().getConfig();
		if(!conf.getBoolean("alert.command.active") || conf.getInt("alert.command.reliability_need") > reliability)
			return;
		for(String s : conf.getStringList("alert.command.run")) {
			Adapter.getAdapter().runConsoleCommand(UniversalUtils.replacePlaceholders(s, "%name%",
					p.getName(), "%uuid%", p.getUniqueId().toString(), "%cheat_key%", c.getKey().toLowerCase(), "%cheat_name%",
					c.getName(), "%reliability%", reliability, "%report_type%", type.name()));
		}
	}

	public static void sendAlertMessage(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		Cheat c = alert.getCheat();
		int reliability = alert.getReliability();
		if(reliability == 0) {// alert already sent
			np.ALERT_NOT_SHOWED.remove(c);
			return;
		}
		Player p = alert.getPlayer();
		int ping = alert.getPing();
		if(alert.getNbAlertConsole() > 0 && log_console) {
			Adapter.getAdapter().getLogger().info("New " + alert.getReportType().getName() + " for " + p.getName()
						+ " (UUID: " + p.getUniqueId().toString() + ") (ping: " + ping + ") : suspected of cheating ("
						+ c.getName() + ") " + (alert.getNbAlertConsole() > 1 ? alert.getNbAlertConsole() + " times " : "") + "Reliability: " + reliability);
		}
		CheatHover hoverMsg = alert.getHover();
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			sendAlertMessage(p, c.getName(), reliability, ping, hoverMsg, alert.getNbAlert());
			np.ALERT_NOT_SHOWED.remove(c);
		} else {
			boolean hasPermPeople = false;
			for (Player pl : Adapter.getAdapter().getOnlinePlayers()) {
				NegativityPlayer npMod = NegativityPlayer.getNegativityPlayer(pl);
				boolean basicPerm = Perm.hasPerm(npMod, Perm.SHOW_ALERT);
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(pl, np, basicPerm);
				EventManager.callEvent(permissionEvent);
				if (permissionEvent.isCancelled() || npMod.disableShowingAlert)
					continue;
				if (permissionEvent.hasBasicPerm()) {
					Adapter.getAdapter().sendMessageRunnableHover(pl, Messages.getMessage(pl, alert.getAlertMessageKey(), "%name%", p.getName(), "%cheat%", c.getName(),
									"%reliability%", String.valueOf(reliability), "%nb%", String.valueOf(alert.getNbAlert())),
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
				np.ALERT_NOT_SHOWED.remove(c);
				Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			}
		}
	}

	private static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount) {
		try {
			AlertMessage alertMessage = new AlertMessage(p.getName(), cheatName, reliability, ping, hover, alertsCount);
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(alertMessage));
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().severe("Could not send alert message to the proxy.");
			e.printStackTrace();
		}
	}

	private static void logProof(NegativityPlayer np, ReportType type, Player p, Cheat c, int reliability,
			String checkName, String proof, int ping) {
		if(!log)
			return;
		String time = new Timestamp(System.currentTimeMillis()).toString().split("\\.")[0];
		np.logProof(time + ": (" + ping + "ms) " + reliability + "% " + c.getKey()
				+ " > " + proof + " | Warn: " + np.getWarn(c) + ", Version: " + p.getPlayerVersion().name() + ". TPS: " + getVisualTPS());
	}
	
	private static String getVisualTPS() {
		StringJoiner sj = new StringJoiner("/");
		for(double d : Adapter.getAdapter().getTPS())
			sj.add(String.format("%.4f", d));
		return sj.toString();
	}
	
	public static void loadNegativity() {
		Adapter ada = Adapter.getAdapter();
		ada.getLogger().info("Thanks for buying Negativity <3");
		
		EventManager.load();
		DefaultConfigValue.init();
		Cheat.loadCheat();
		Special.loadSpecial();
		Database.init();
		Perm.init();
		BanManager.init();
		TranslatedMessages.init();
		NegativityAccountStorage.init();
		VerificationManager.init();
		BypassManager.loadBypass();
		UniversalUtils.init();
		
		Configuration config = ada.getConfig();
		log = config.getBoolean("log_alerts");
		log_console = config.getBoolean("log_alerts_in_console");
		hasBypass = config.getBoolean("Permissions.bypass.active");
		
		StringJoiner supportedPluginName = new StringJoiner(", ");
		if (ada.hasPlugin("Essentials")) {
			essentialsSupport = true;
			supportedPluginName.add("Essentials");
		}
		if (ada.hasPlugin("WorldGuard")) {
			worldGuardSupport = true;
			supportedPluginName.add("WorldGuard");
		}
		if (ada.hasPlugin("GadgetsMenu")) {
			gadgetMenuSupport = true;
			supportedPluginName.add("GadgetsMenu");
		}

		if (ada.hasPlugin("MaxBans")) {
			BanManager.registerProcessor("maxbans", new MaxBansProcessor());
			supportedPluginName.add("MaxBans");
		}

		if (ada.hasPlugin("AdvancedBan")) {
			BanManager.registerProcessor("advancedban", new AdvancedBanProcessor());
			supportedPluginName.add("AdvancedBan");
		}

		if (ada.hasPlugin("LiteBans")) {
			BanManager.registerProcessor("litebans", new LiteBansProcessor());
			supportedPluginName.add("LiteBans");
		}
		
		if (ada.hasPlugin("ViaVersion")) {
			viaVersionSupport = true;
			supportedPluginName.add("ViaVersion");
		}
		
		if (ada.hasPlugin("ProtocolSupport")) {
			protocolSupportSupport = true;
			supportedPluginName.add("ProtocolSupport");
		}
		
		if (ada.hasPlugin("floodgate-bukkit")) {
			Negativity.floodGateSupport = true;
			supportedPluginName.add("FloodGate");
		}
		
		if (supportedPluginName.length() > 0) {
			ada.getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}
	}

	public static void sendReportMessage(Player reporter, String reason, String reported) {
		try {
			ReportMessage reportMessage = new ReportMessage(reported, reason, reporter.getName());
			reporter.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(reportMessage));
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().severe("Could not send report message to the proxy.");
			e.printStackTrace();
		}
	}
}
