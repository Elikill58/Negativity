package com.elikill58.negativity.common;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;

public class GameEventsManager implements Listeners {

	@EventListener
	public void onProxyPlugin(ProxyPluginListEvent e) {
		Adapter.getAdapter().getOnlinePlayers().forEach(this::sendVersionRequest);
	}

	@EventListener
	public void onJoin(PlayerConnectEvent e) {
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			sendVersionRequest(e.getPlayer());
		}
	}

	private void sendVersionRequest(Player p) {
		p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new PlayerVersionMessage(p.getUniqueId(), null));
	}
	
	@EventListener
	public void onChannelMessage(GameChannelNegativityMessageEvent e) {
		Player p = e.getPlayer();
		NegativityMessage message = e.getMessage();
		if (message instanceof ProxyPingMessage) {
			ProxyPingMessage pingMessage = (ProxyPingMessage) message;
			ProxyCompanionManager.foundCompanion(pingMessage);
		} else if (message instanceof ClientModsListMessage) {
			ClientModsListMessage modsMessage = (ClientModsListMessage) message;
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			if(!modsMessage.getMods().isEmpty()) {
				np.MODS.clear();
				np.MODS.putAll(modsMessage.getMods());
			}
		} else if(message instanceof PlayerVersionMessage) {
			p.setPlayerVersion(((PlayerVersionMessage) message).getVersion());
		} else {
			Adapter.getAdapter().getLogger().warn("Received unexpected plugin message " + message.getClass().getName());
		}
	}
}
