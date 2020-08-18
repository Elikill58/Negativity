package com.elikill58.negativity.velocity;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AccountUpdateMessage;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.velocity.impl.entity.VelocityPlayer;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
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
			for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers()) {
				NegativityPlayer nPlayer = NegativityPlayer.getCached(pp.getUniqueId());
				if (Perm.hasPerm(nPlayer, Perm.SHOW_ALERT)) {
					Builder msg = TextComponent.builder();
					msg.append(VelocityMessages.getMessage(pp, alertMessageKey, place));

					Builder hoverMessage = VelocityMessages.getMessage(pp, "alert_hover", place).color(TextColor.GOLD).toBuilder();
					Cheat.CheatHover hoverInfo = alert.getHoverInfo();
					if (hoverInfo != null) {
						hoverMessage.append(TextComponent.newline())
								.append(TextComponent.newline())
								.resetStyle()
								.append(VelocityMessages.coloredBungeeMessage(hoverInfo.compile(nPlayer)));
					}

					hoverMessage.append(TextComponent.newline())
							.append(TextComponent.newline())
							.append(VelocityMessages.getMessage(pp, "alert_tp_info", "%playername%", alert.getPlayername()));

					msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
					msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
					pp.sendMessage(msg.build());
				}
			}
		} else if (message instanceof ProxyPingMessage) {
			p.getCurrentServer().ifPresent(server -> {
				try {
					server.sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION)));
				} catch (IOException e) {
					VelocityNegativity.getInstance().getLogger().error("Could not write PingProxyMessage.", e);
				}
			});
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[] { "%name%", report.getReported(), "%reason%", report.getReason(), "%report%", report.getReporter() };
			boolean hasPermitted = false;
			for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers()) {
				if (Perm.hasPerm(NegativityPlayer.getCached(pp.getUniqueId()), Perm.SHOW_REPORT)) {
					hasPermitted = true;
					Builder msg = TextComponent.builder();
					msg.append(VelocityMessages.getMessage(pp, "report", place));
					msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, VelocityMessages.getMessage(pp, "report_hover", "%playername%", report.getReported())));
					msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
					pp.sendMessage(msg.build());
				}
			}
			if (!hasPermitted) {
				NegativityListener.report.add(new Report("/server " + p.getCurrentServer().get().getServerInfo().getName(), place));
			}
		} else if (message instanceof ProxyExecuteBanMessage) {
			ProxyExecuteBanMessage banMessage = (ProxyExecuteBanMessage) message;
			BanManager.executeBan(banMessage.getBan());
		} else if (message instanceof ProxyRevokeBanMessage) {
			ProxyRevokeBanMessage revocationMessage = (ProxyRevokeBanMessage) message;
			BanManager.revokeBan(revocationMessage.getPlayerId());
		} else if (message instanceof AccountUpdateMessage) {
			AccountUpdateMessage accountUpdateMessage = (AccountUpdateMessage) message;
			NegativityAccount account = accountUpdateMessage.getAccount();
			Adapter.getAdapter().getAccountManager().update(account);
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
	public void onLogin(LoginEvent event) {
		Player p = event.getPlayer();
		Ban activeBan = BanManager.getActiveBan(p.getUniqueId());
		if (activeBan != null) {
			String kickMsgKey = activeBan.isDefinitive() ? "ban.kick_def" : "ban.kick_time";
			LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(activeBan.getExpirationTime()), ZoneId.systemDefault());
			String formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(expirationDateTime);
			TextComponent banMessage = VelocityMessages.getMessage(p, kickMsgKey, "%reason%", activeBan.getReason(), "%time%", formattedExpiration, "%by%", activeBan.getBannedBy());
			event.setResult(ResultedEvent.ComponentResult.denied(banMessage));
			Adapter.getAdapter().getAccountManager().dispose(p.getUniqueId());
		}
	}

	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(new VelocityPlayer(p));
		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
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
				.buildTask(plugin, () -> {
					UUID playerId = event.getPlayer().getUniqueId();
					NegativityPlayer.removeFromCache(playerId);
					NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
					accountManager.save(playerId);
					accountManager.dispose(playerId);
				})
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
