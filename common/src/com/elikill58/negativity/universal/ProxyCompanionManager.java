package com.elikill58.negativity.universal;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.plugins.ProxyPluginListEvent;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;

public class ProxyCompanionManager {

	private static boolean foundCompanion = false, protocolVersionMismatch = false, forceCompagnion = false;
	public static boolean searchedCompanion = false;

	public static boolean isIntegrationEnabled() {
		return forceCompagnion || (foundCompanion && !protocolVersionMismatch && !Adapter.getAdapter().getConfig().getBoolean("disableProxyIntegration"));
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
			ada.getLogger().warn("Proxy companion plugin found BUT :");
			ada.getLogger().warn("Protocol version (" + protocolVersion + ") is not the same a ours (" + ourProtocolVersion + ") so it won't be used.");
			ada.getLogger().warn("Please upgrade your version of Negativity on proxy !");
		} else {
			ada.getLogger().info("Proxy companion plugin found, it will be used. " + message.getPlugins().size() + " plugins founded on proxy.");
			EventManager.callEvent(new ProxyPluginListEvent(message.getPlugins()));
		}
	}
	
	public static void forceCompanion() {
		forceCompagnion = true;
		searchedCompanion = true;
	}
}
