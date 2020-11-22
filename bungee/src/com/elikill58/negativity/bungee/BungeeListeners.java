package com.elikill58.negativity.bungee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.bungee.impl.entity.BungeePlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("deprecation")
public class BungeeListeners implements Listener {

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
				Adapter.getAdapter().getLogger().warn(warnMessage);
				return;
			}
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not read plugin message: " + e.getMessage());
			return;
		}

		ProxiedPlayer player = (ProxiedPlayer) (event.getSender() instanceof ProxiedPlayer ? event.getSender() : (event.getReceiver() instanceof ProxiedPlayer ? event.getReceiver() : null));
		if (player == null) {
			Adapter.getAdapter().getLogger().warn("Error while receiving a plugin message." +
					" Player null (Sender: " + event.getSender() + " Receiver: " + event.getReceiver() + ")");
			return;
		}

		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			Object[] place = new Object[]{"%name%", alert.getPlayername(), "%cheat%", alert.getCheat(),
					"%reliability%", alert.getReliability(), "%ping%", alert.getPing(), "%nb%", alert.getAlertsCount()};
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				NegativityPlayer nPlayer = NegativityPlayer.getCached(pp.getUniqueId());
				if (Perm.hasPerm(nPlayer, Perm.SHOW_ALERT)) {
					TextComponent alertMessage = new TextComponent(Messages.getMessage(pp.getUniqueId(), alertMessageKey, place));

					ComponentBuilder hoverComponent = new ComponentBuilder(Messages.getMessage(pp.getUniqueId(), "alert_hover", place));
					Cheat.CheatHover hoverInfo = alert.getHoverInfo();
					if (hoverInfo != null) {
						hoverComponent.append("\n\n" + Messages.getMessage(hoverInfo.compile(nPlayer)), ComponentBuilder.FormatRetention.NONE);
					}
					hoverComponent.append("\n\n" + Messages.getMessage(pp.getUniqueId(), "alert_tp_info", "%playername%", alert.getPlayername()), ComponentBuilder.FormatRetention.NONE);
					alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent.create()));

					String tpCommand = pp.getServer().equals(player.getServer()) ? "/tp " + alert.getPlayername() : "/server " + player.getServer().getInfo().getName();
					alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand));
					pp.sendMessage(alertMessage);
				}
			}
		} else if (message instanceof ProxyPingMessage) {
			try {
				player.getServer().sendData(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION)));
			} catch (IOException e) {
				Adapter.getAdapter().getLogger().error("Could not write PingProxyMessage: " + e.getMessage());
			}
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[]{"%name%", report.getReported(), "%reason%", report.getReason(), "%report%", report.getReporter()};
			boolean hasPermitted = false;
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers())
				if (Perm.hasPerm(NegativityPlayer.getCached(pp.getUniqueId()), Perm.SHOW_REPORT)) {
					hasPermitted = true;
					TextComponent msg = new TextComponent(Messages.getMessage(pp.getUniqueId(), "report", place));
					msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(Messages.getMessage(pp.getUniqueId(), "report_hover", "%playername%", report.getReported()))}));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, pp.getServer().equals(player.getServer()) ? "/tp " + pp.getName() : "/server " + player.getServer().getInfo().getName()));
					pp.sendMessage(msg);
				}
			if (!hasPermitted) {
				BungeeListeners.report.add(new Report("/server " + player.getServer().getInfo().getName(), place));
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
			Adapter.getAdapter().getLogger().warn("Unhandled plugin message " + message.getClass());
		}
	}
	
	@EventHandler
	public void onPreLogin(net.md_5.bungee.api.event.LoginEvent e) {
		PendingConnection co = e.getConnection();
		LoginEvent event = new LoginEvent(co.getUniqueId(), co.getName(), e.isCancelled() ? Result.KICK_BANNED : Result.ALLOWED,
				co.getAddress().getAddress(), getReason(e));
		EventManager.callEvent(event);
		if(!event.getLoginResult().equals(Result.ALLOWED)) {
			e.setCancelled(true);
			e.setCancelReason(new ComponentBuilder(event.getKickMessage()).create());
		}
	}
	
	private String getReason(net.md_5.bungee.api.event.LoginEvent e) {
		BaseComponent[] comp = e.getCancelReasonComponents();
		if(comp == null || comp.length == 0)
			return "";
		return comp[0].toPlainText();
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onServerChange(ServerConnectedEvent event) {
		try {
			ClientModsListMessage message = new ClientModsListMessage(event.getPlayer().getModList());
			event.getServer().sendData(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(message));
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage : " + e.getMessage());
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
			TextComponent msg = new TextComponent(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
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
