package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;

public class ProxyCompanionManager {

	public static boolean searchedCompanion = false;
	public static boolean foundCompanion = false;
	public static boolean protocolVersionMismatch = false;
	public static List<String> pluginsProxy = new ArrayList<>();

	public static boolean isIntegrationEnabled() {
		return foundCompanion && !protocolVersionMismatch && !Adapter.getAdapter().getConfig().getBoolean("disableProxyIntegration");
	}

	public static void foundCompanion(ProxyPingMessage message) {
		foundCompanion = true;
		
		int protocolVersion = message.getProtocol();
		int ourProtocolVersion = NegativityMessagesManager.PROTOCOL_VERSION;
		Adapter ada = Adapter.getAdapter();
		if (ada.getConfig().getBoolean("disableProxyIntegration", false)) {
			ada.getLogger().info("Proxy companion plugin found, but is forcibly disabled.");
		} else if (protocolVersion != ourProtocolVersion) {
			protocolVersionMismatch = true;
			ada.getLogger().info("Proxy companion plugin found, but its protocol version (" + protocolVersion + ") is not the same a ours (" + ourProtocolVersion + ") so it won't be used.");
		} else {
			ada.getLogger().info("Proxy companion plugin found, it will be used. " + message.getPlugins().size() + " plugins founded on proxy.");
			pluginsProxy = message.getPlugins();
			EventManager.callEvent(new ProxyPluginListEvent(pluginsProxy));
		}
	}
}
