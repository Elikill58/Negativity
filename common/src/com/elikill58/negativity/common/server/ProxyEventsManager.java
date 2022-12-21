package com.elikill58.negativity.common.server;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.channel.ProxyChannelNegativityMessageEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AccountUpdateMessage;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteWarnMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.pluginMessages.ShowAlertStatusMessage;
import com.elikill58.negativity.universal.warn.WarnManager;

public class ProxyEventsManager implements Listeners {

	public static final List<ReportMessage> REPORTS = new ArrayList<>();

	@EventListener
	public void onChannelMessage(ProxyChannelNegativityMessageEvent e) {
		Player p = e.getPlayer();
		NegativityMessage message = e.getMessage();
		Adapter ada = Adapter.getAdapter();
		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			Player cible = ada.getPlayer(alert.getPlayerUUID());
			String playername = cible.getName();
			String serverName = cible.getServerName();
			Object[] place = new Object[] { "%name%", playername, "%cheat%", alert.getCheat(), "%server_name%",
					serverName, "%reliability%", alert.getReliability(), "%ping%", alert.getPing(), "%nb%",
					alert.getAlertsCount() };
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (Player mod : ada.getOnlinePlayers()) {
				NegativityPlayer nPlayer = NegativityPlayer.getNegativityPlayer(mod);
				if (Perm.hasPerm(mod, Perm.SHOW_ALERT) && nPlayer.getAccount().isShowAlert()) {
					ada.debug("Sending alert to " + mod.getName());
					String msg = Messages.getMessage(mod, alertMessageKey, place);
					String hover = Messages.getMessage(mod, "alert_hover", place);
					Cheat.CheatHover hoverInfo = alert.getHoverInfo();
					if (hoverInfo != null) {
						hover += "\n\n";
						hover += Messages.getMessage(hoverInfo.compile(nPlayer));
					}
					hover += "\n\n";
					hover += Messages.getMessage(mod, "alert_tp_info", "%playername%", playername);
					ada.sendMessageRunnableHover(mod, msg, hover, "/negativitytp " + p.getName());
				} else
					ada.debug("Player " + mod.getName() + ", show: " + nPlayer.getAccount().isShowAlert());
			}
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[] { "%name%", report.getReported(), "%reason%", report.getReason(), "%report%",
					report.getReporter() };
			boolean hasPermitted = false;
			for (Player mod : ada.getOnlinePlayers()) {
				if (Perm.hasPerm(mod, Perm.SHOW_REPORT)) {
					hasPermitted = true;
					ada.sendMessageRunnableHover(mod, Messages.getMessage(mod, "report", place),
							Messages.getMessage(mod, "report_hover", "%playername%", report.getReported()), "/negativitytp " + p.getName());
				}
			}
			if (!hasPermitted)
				REPORTS.add(report);
		} else if (message instanceof ProxyExecuteWarnMessage) {
			ProxyExecuteWarnMessage warnMessage = (ProxyExecuteWarnMessage) message;
			WarnManager.executeWarn(warnMessage.getWarn());
		} else if (message instanceof ProxyExecuteBanMessage) {
			ProxyExecuteBanMessage banMessage = (ProxyExecuteBanMessage) message;
			BanManager.executeBan(banMessage.getBan());
		} else if (message instanceof ProxyRevokeBanMessage) {
			ProxyRevokeBanMessage revocationMessage = (ProxyRevokeBanMessage) message;
			BanManager.revokeBan(revocationMessage.getPlayerId());
		} else if (message instanceof AccountUpdateMessage) {
			AccountUpdateMessage accountUpdateMessage = (AccountUpdateMessage) message;
			ada.getAccountManager().update(accountUpdateMessage.getAccount());
		} else if (message instanceof PlayerVersionMessage) {
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID,
					new PlayerVersionMessage(p.getUniqueId(), Version.getVersionByProtocolID(p.getProtocolVersion())));
		} else if (message instanceof ShowAlertStatusMessage) {
			ShowAlertStatusMessage msg = (ShowAlertStatusMessage) message;
			NegativityAccount.get(msg.getUUID()).setShowAlert(msg.isShowAlert());
		} else if (message instanceof ProxyPingMessage) {
			Adapter.getAdapter().debug("Received proxy ping from " + p.getName() + ": " + p.getServerName());
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID,
					new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION, ada.getAllPlugins()));
		} else
			ada.getLogger().warn("Unhandled plugin message: " + message.getClass().getName());
	}
}
