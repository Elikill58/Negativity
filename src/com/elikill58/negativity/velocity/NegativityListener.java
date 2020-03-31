package com.elikill58.negativity.velocity;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.util.ModInfo;

import net.kyori.text.TextComponent;
import net.kyori.text.TextComponent.Builder;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;

public class NegativityListener {

	public static List<Report> report = new ArrayList<>();

	@Subscribe
	public void onMessageReceived(PluginMessageEvent event) {
		if (!event.getIdentifier().equals(VelocityNegativity.NEGATIVITY_CHANNEL_ID)) {
			return;
		}

		event.setResult(PluginMessageEvent.ForwardResult.handled());

		NegativityMessage message;
		try {
			message = NegativityMessagesManager.readMessage(event.getData());
			if (message == null) {
				VelocityNegativity.getInstance().getLogger().warn(
						"Received unknown plugin message from channel {} sent by {} to {}",
						event.getIdentifier().getId(), event.getSource(), event.getTarget());
				return;
			}
		} catch (IOException e) {
			VelocityNegativity.getInstance().getLogger().error("Could not read plugin message.", e);
			return;
		}

		Player p = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
		if (p == null) {
			VelocityNegativity.getInstance().getLogger().error("Source and Target not proxied (Source: {} Target: {})", event.getSource(), event.getTarget());
			return;
		}

		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			Object[] place = new Object[]{"%name%", alert.getPlayername(), "%cheat%", alert.getCheat(),
					"%reliability%", alert.getReliability(), "%ping%", alert.getPing(), "%nb%", alert.getAlertsCount()};
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers())
				if (Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer(pp), "showAlert")) {
					Builder msg = TextComponent.builder();
					msg.append(VelocityMessages.getMessage(pp, alertMessageKey, place));

					Builder hoverMessage = VelocityMessages.getMessage(pp, "alert_hover", place).color(TextColor.GOLD).toBuilder();
					if (!alert.getHoverInfo().isEmpty()) {
						hoverMessage.append(TextComponent.newline())
								.append(TextComponent.newline())
								.resetStyle()
								.append(VelocityMessages.coloredBungeeMessage(alert.getHoverInfo()));
					}

					hoverMessage.append(TextComponent.newline())
							.append(TextComponent.newline())
							.append(VelocityMessages.getMessage(pp, "alert_tp_info", "%playername%", alert.getPlayername()));

					msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
					msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
					pp.sendMessage(msg.build());
				}
		} else if (message instanceof ProxyPingMessage) {
			p.getCurrentServer().ifPresent(server -> {
				try {
					server.sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, NegativityMessagesManager.writeMessage(new ProxyPingMessage()));
				} catch (IOException e) {
					VelocityNegativity.getInstance().getLogger().error("Could not write PingProxyMessage.", e);
				}
			});
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[] { "%name%", report.getReported(), "%reason%", report.getReason(), "%report%", report.getReporter() };
			boolean hasPermitted = false;
			for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers()) {
				if (Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer(pp), "showReport")) {
					hasPermitted = true;
					Builder msg = TextComponent.builder();
					msg.append(VelocityMessages.getMessage(pp, "report", place));
					msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
					pp.sendMessage(msg.build());
				}
			}
			if (!hasPermitted) {
				NegativityListener.report.add(new Report("/server " + p.getCurrentServer().get().getServerInfo().getName(), place));
			}
		} else {
			VelocityNegativity.getInstance().getLogger().warn("Unhandled plugin message: {}.", message.getClass().getName());
		}
	}

	private String getCommand(Player targetPlayer, Player notifiedPlayer) {
		ServerInfo targetPlayerServer = targetPlayer.getCurrentServer().get().getServerInfo();
		ServerInfo notifiedPlayerServer = notifiedPlayer.getCurrentServer().get().getServerInfo();
		if (targetPlayerServer.equals(notifiedPlayerServer)) {
			return "/tp " + targetPlayer.getUsername();
		}
		return "/server " + notifiedPlayerServer.getName();
	}

	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		Ban activeBan = BanManager.getActiveBan(p.getUniqueId());
		if (activeBan != null) {
			String kickMsgKey = activeBan.isDefinitive() ? "ban.kick_def" : "ban.kick_time";
			String formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(activeBan.getExpirationTime()));
			p.disconnect(VelocityMessages.getMessage(e.getPlayer(), kickMsgKey, "%reason%", activeBan.getReason(), "%time%" , formattedExpiration, "%by%", activeBan.getBannedBy()));
			return;
		}

		VelocityNegativityPlayer np = VelocityNegativityPlayer.getNegativityPlayer(p);
		if (Perm.hasPerm(np, "showAlert")) {
			for (Report msg : report) {
				p.sendMessage(msg.toMessage(p));
				report.remove(msg);
			}
		}
	}

	@Subscribe
	public void onPlayerQuit(DisconnectEvent event) {
		VelocityNegativity plugin = VelocityNegativity.getInstance();
		plugin.getServer().getScheduler()
				.buildTask(plugin, () -> VelocityNegativityPlayer.removeFromCache(event.getPlayer().getUniqueId()))
				.delay(1, TimeUnit.SECONDS)
				.schedule();
	}

	@Subscribe
	public void onServerChange(ServerConnectedEvent event) {
		List<ModInfo.Mod> modsList = event.getPlayer().getModInfo()
				.map(ModInfo::getMods)
				.orElseGet(Collections::emptyList);
		if (modsList.isEmpty()) {
			return;
		}

		Map<String, String> mods = new HashMap<>();
		for (ModInfo.Mod mod : modsList) {
			mods.put(mod.getId(), mod.getVersion());
		}

		try {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new ClientModsListMessage(mods));
			event.getServer().sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, rawMessage);
		} catch (IOException e) {
			VelocityNegativity.getInstance().getLogger().error("Could not write ClientModsListMessage.", e);
		}
	}

	public static class Report {

		private Object[] place;
		private String cmd;

		public Report(String cmd, Object... parts) {
			place = new Object[] { "%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2], "%ping%",
					parts[3] };
			this.cmd = cmd;
		}

		public TextComponent toMessage(Player p) {
			Builder msg = TextComponent.builder(VelocityMessages.getStringMessage(p, "alert", place));
			String hover = VelocityMessages.getStringMessage(p, "alert_hover", place);
			if (hover.contains("\\n")) {
				Builder hoverMessage = TextComponent.builder("");
				hoverMessage.color(TextColor.GOLD);
				for(String s : hover.split("\\n"))
					hoverMessage.append(TextComponent.builder(s));
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
			} else
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.builder(hover).build()));
			msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg.build();
		}
	}
}
