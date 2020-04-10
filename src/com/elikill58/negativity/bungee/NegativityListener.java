package com.elikill58.negativity.bungee;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

public class NegativityListener implements Listener {

	public static List<Report> report = new ArrayList<>();

	@EventHandler
	public void onMessageReceived(PluginMessageEvent event) {
		if (!event.getTag().toLowerCase().contains("negativity"))
			return;

		event.setCancelled(true);

		NegativityMessage message;
		try {
			message = NegativityMessagesManager.readMessage(event.getData());
			if (message == null) {
				String warnMessage = String.format("Received unknown plugin message. Channel %s send by %s to %s.",
						event.getTag(), event.getSender(), event.getReceiver());
				BungeeNegativity.getInstance().getLogger().warning(warnMessage);
				return;
			}
		} catch (IOException e) {
			BungeeNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not read plugin message.", e);
			return;
		}

		ProxiedPlayer player = (ProxiedPlayer) (event.getSender() instanceof ProxiedPlayer ? event.getSender() : (event.getReceiver() instanceof ProxiedPlayer ? event.getReceiver() : null));
		if (player == null) {
			BungeeNegativity.getInstance().getLogger().warning("Error while receiving a plugin message." +
					" Player null (Sender: " + event.getSender() + " Receiver: " + event.getReceiver() + ")");
			return;
		}

		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			Object[] place = new Object[]{"%name%", alert.getPlayername(), "%cheat%", alert.getCheat(),
					"%reliability%", alert.getReliability(), "%ping%", alert.getPing(), "%nb%", alert.getAlertsCount()};
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers())
				if (Perm.hasPerm(BungeeNegativityPlayer.getNegativityPlayer(pp), "showAlert")) {
					TextComponent alertMessage = new TextComponent(BungeeMessages.getMessage(pp, alertMessageKey, place));

					ComponentBuilder hoverComponent = new ComponentBuilder(BungeeMessages.getMessage(pp, "alert_hover", place));
					if (!alert.getHoverInfo().isEmpty()) {
						hoverComponent.append("\n\n" + BungeeMessages.coloredBungeeMessage(alert.getHoverInfo()), ComponentBuilder.FormatRetention.NONE);
					}
					hoverComponent.append("\n\n" + BungeeMessages.getMessage(pp, "alert_tp_info", "%playername%", alert.getPlayername()), ComponentBuilder.FormatRetention.NONE);
					alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent.create()));

					String tpCommand = pp.getServer().equals(player.getServer()) ? "/tp " + alert.getPlayername() : "/server " + player.getServer().getInfo().getName();
					alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand));
					pp.sendMessage(alertMessage);
				}
		} else if (message instanceof ProxyPingMessage) {
			try {
				player.getServer().sendData(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION)));
			} catch (IOException e) {
				BungeeNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not write PingProxyMessage.", e);
			}
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[]{"%name%", report.getReported(), "%reason%", report.getReason(), "%report%", report.getReporter()};
			boolean hasPermitted = false;
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers())
				if (Perm.hasPerm(BungeeNegativityPlayer.getNegativityPlayer(pp), "showReport")) {
					hasPermitted = true;
					TextComponent msg = new TextComponent(BungeeMessages.getMessage(pp, "report", place));
					msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(BungeeMessages.getMessage(pp, "report_hover", "%playername%", report.getReported()))}));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, pp.getServer().equals(player.getServer()) ? "/tp " + pp.getName() : "/server " + player.getServer().getInfo().getName()));
					pp.sendMessage(msg);
				}
			if (!hasPermitted) {
				NegativityListener.report.add(new Report("/server " + player.getServer().getInfo().getName(), place));
			}
		} else {
			BungeeNegativity.getInstance().getLogger().log(Level.WARNING, "Unhandled plugin message %s.", message.getClass());
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		Ban activeBan = BanManager.getActiveBan(p.getUniqueId());
		if (activeBan != null) {
			String kickMsgKey = activeBan.isDefinitive() ? "ban.kick_def" : "ban.kick_time";
			String formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(activeBan.getExpirationTime()));
			p.disconnect(new ComponentBuilder(BungeeMessages.getMessage(e.getPlayer(), kickMsgKey, "%reason%", activeBan.getReason(), "%time%" , formattedExpiration, "%by%", activeBan.getBannedBy())).create());
			return;
		}

		BungeeNegativityPlayer np = BungeeNegativityPlayer.getNegativityPlayer(p);
		if (Perm.hasPerm(np, "showAlert"))
			for (Report msg : report) {
				p.sendMessage(msg.toMessage(p));
				report.remove(msg);
			}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxyServer.getInstance().getScheduler().schedule(BungeeNegativity.getInstance(),
				() -> BungeeNegativityPlayer.removeFromCache(event.getPlayer().getUniqueId()),
				1, TimeUnit.SECONDS);
	}

	@EventHandler
	public void onServerChange(ServerConnectedEvent event) {
		try {
			ClientModsListMessage message = new ClientModsListMessage(event.getPlayer().getModList());
			event.getServer().sendData(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(message));
		} catch (IOException e) {
			BungeeNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not write ClientModsListMessage.", e);
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

		public TextComponent toMessage(ProxiedPlayer p) {
			TextComponent msg = new TextComponent(BungeeMessages.getMessage(p, "alert", place));
			String hover = BungeeMessages.getMessage(p, "alert_hover", place);
			if (hover.contains("\\n")) {
				ArrayList<TextComponent> components = new ArrayList<>();
				TextComponent hoverMessage = new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 2]).color(ChatColor.GOLD)
								.create());
				hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
				hoverMessage.addExtra(new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 1]).create()));
				components.add(hoverMessage);
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						(BaseComponent[]) components.toArray(new BaseComponent[components.size()])));
			} else
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg;
		}
	}
}
