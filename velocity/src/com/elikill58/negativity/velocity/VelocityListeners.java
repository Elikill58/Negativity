package com.elikill58.negativity.velocity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
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
import com.elikill58.negativity.velocity.impl.entity.VelocityPlayer;
import com.velocitypowered.api.event.ResultedEvent;
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

public class VelocityListeners {

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
				Adapter.getAdapter().getLogger().warn("Received unknown plugin message from channel "
						+ event.getIdentifier().getId() + " sent by " + event.getSource() + " to " + event.getTarget());
				return;
			}
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not read plugin message : " + e.getMessage());
			return;
		}

		Player p = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
		if (p == null) {
			Adapter.getAdapter().getLogger().error("Source and Target not proxied (Source: " + event.getSource() + " Target: " + event.getTarget() + ")");
			return;
		}

		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			Object[] place = new Object[]{"%name%", alert.getPlayername(), "%cheat%", alert.getCheat(),
					"%reliability%", alert.getReliability(), "%ping%", alert.getPing(), "%nb%", alert.getAlertsCount()};
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (com.elikill58.negativity.api.entity.Player commonPlayer : Adapter.getAdapter().getOnlinePlayers()) {
				NegativityPlayer nPlayer = NegativityPlayer.getNegativityPlayer(commonPlayer);
				if (Perm.hasPerm(nPlayer, Perm.SHOW_ALERT)) {
					Player pp = (Player) commonPlayer.getDefault();
					Builder msg = TextComponent.builder();
					msg.append(Messages.getMessage(pp.getUniqueId(), alertMessageKey, place));

					Builder hoverMessage = TextComponent.builder(Messages.getMessage(pp.getUniqueId(), "alert_hover", place), TextColor.GOLD);
					Cheat.CheatHover hoverInfo = alert.getHoverInfo();
					if (hoverInfo != null) {
						hoverMessage.append(TextComponent.newline())
								.append(TextComponent.newline())
								.resetStyle()
								.append(Messages.getMessage(hoverInfo.compile(nPlayer)));
					}

					hoverMessage.append(TextComponent.newline())
							.append(TextComponent.newline())
							.append(Messages.getMessage(pp.getUniqueId(), "alert_tp_info", "%playername%", alert.getPlayername()));

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
					Adapter.getAdapter().getLogger().error("Could not write PingProxyMessage: " + e.getMessage());
				}
			});
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[] { "%name%", report.getReported(), "%reason%", report.getReason(), "%report%", report.getReporter() };
			boolean hasPermitted = false;
			for (com.elikill58.negativity.api.entity.Player commonPlayer : Adapter.getAdapter().getOnlinePlayers()) {
				if (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(commonPlayer), Perm.SHOW_REPORT)) {
					hasPermitted = true;
					Player pp = (Player) commonPlayer.getDefault();
					Builder msg = TextComponent.builder();
					msg.append(Messages.getMessage(pp.getUniqueId(), "report", place));
					msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of(Messages.getMessage(pp.getUniqueId(), "report_hover", "%playername%", report.getReported()))));
					msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
					pp.sendMessage(msg.build());
				}
			}
			if (!hasPermitted) {
				VelocityListeners.report.add(new Report("/server " + p.getCurrentServer().get().getServerInfo().getName(), place));
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
			Adapter.getAdapter().getLogger().warn("Unhandled plugin message: " + message.getClass().getName());
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
	public void onLogin(com.velocitypowered.api.event.connection.LoginEvent e) {
		Player p = e.getPlayer();
		LoginEvent event = new LoginEvent(p.getUniqueId(), p.getUsername(), e.getResult().isAllowed() ? Result.ALLOWED : Result.KICK_BANNED, p.getRemoteAddress().getAddress(), "");
		EventManager.callEvent(event);
		if(!event.getLoginResult().equals(Result.ALLOWED))
			e.setResult(ResultedEvent.ComponentResult.denied(TextComponent.of(event.getKickMessage())));
	}

	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@Subscribe
	public void onPlayerQuit(DisconnectEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
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
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage: " + e.getMessage());
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
			Builder msg = TextComponent.builder(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
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
