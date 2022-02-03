package com.elikill58.negativity.common;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.channel.ProxyChannelNegativityMessageEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.pluginMessages.AccountUpdateMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ShowAlertStatusMessage;

public class ProxyEventsManager implements Listeners {

	@EventListener
	public void onProxyPlugin(ProxyPluginListEvent e) {
		Adapter.getAdapter().getOnlinePlayers().forEach(this::sendVersionRequest);
	}
	
	@EventListener
	public void onJoin(PlayerConnectEvent e) {
		if(ProxyCompanionManager.isIntegrationEnabled()) {
			sendVersionRequest(e.getPlayer());
		}
	}
	
	private void sendVersionRequest(Player p) {
		p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new PlayerVersionMessage(p.getUniqueId(), null));
	}
	
	@EventListener
	public void onChannelMessage(ProxyChannelNegativityMessageEvent e) {
		Player p = e.getPlayer();
		NegativityMessage message = e.getMessage();
		if (message instanceof ProxyExecuteBanMessage) {
			ProxyExecuteBanMessage banMessage = (ProxyExecuteBanMessage) message;
			BanManager.executeBan(banMessage.getBan());
		} else if (message instanceof ProxyRevokeBanMessage) {
			ProxyRevokeBanMessage revocationMessage = (ProxyRevokeBanMessage) message;
			BanManager.revokeBan(revocationMessage.getPlayerId());
		} else if (message instanceof AccountUpdateMessage) {
			AccountUpdateMessage accountUpdateMessage = (AccountUpdateMessage) message;
			Adapter.getAdapter().getAccountManager().update(accountUpdateMessage.getAccount());
		} else if(message instanceof PlayerVersionMessage) {
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new PlayerVersionMessage(p.getUniqueId(), Version.getVersionByProtocolID(p.getProtocolVersion())));
		} else if (message instanceof ShowAlertStatusMessage) {
			ShowAlertStatusMessage msg = (ShowAlertStatusMessage) message;
			NegativityAccount.get(msg.getUUID()).setShowAlert(msg.isShowAlert());
		} else
			Adapter.getAdapter().getLogger().warn("Unhandled plugin message: " + message.getClass().getName());
	}
}
