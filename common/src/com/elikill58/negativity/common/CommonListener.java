package com.elikill58.negativity.common;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.proxysender.ProxySenderManager;
import com.elikill58.negativity.universal.proxysender.hook.RedisBungeeProxySender;

public class CommonListener implements Listeners {

	@EventListener
	public void onPluginList(ProxyPluginListEvent e) {
		if(e.contains("RedisBungee")) {
			Adapter.getAdapter().getLogger().info("RedisBungee found in proxy companion. Loading support ...");
			ProxySenderManager.setProxySender(new RedisBungeeProxySender());
		}
	}
}
