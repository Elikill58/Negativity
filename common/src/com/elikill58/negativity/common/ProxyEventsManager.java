package com.elikill58.negativity.common;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.Adapter;
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
}
