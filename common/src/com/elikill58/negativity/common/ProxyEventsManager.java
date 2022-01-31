package com.elikill58.negativity.common;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;

public class ProxyEventsManager implements Listeners {

	@EventListener
	public void onProxyPlugin(ProxyPluginListEvent e) {
		Adapter.getAdapter().getOnlinePlayers().forEach(p -> {
			try { // send ask version request
				p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(new PlayerVersionMessage(p.getUniqueId(), null)));
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}
	
	@EventListener
	public void onJoin(PlayerConnectEvent e) {
		if(ProxyCompanionManager.isIntegrationEnabled()) {
			try { // send ask version request
				Player p = e.getPlayer();
				p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(new PlayerVersionMessage(p.getUniqueId(), null)));
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
}
