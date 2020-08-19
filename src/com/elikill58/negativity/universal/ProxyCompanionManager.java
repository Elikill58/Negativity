package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;

public class ProxyCompanionManager {

	public static boolean searchedCompanion = false;
	public static boolean foundCompanion = false;
	public static boolean protocolVersionMismatch = false;

	public static boolean isIntegrationEnabled() {
		return foundCompanion && !protocolVersionMismatch && !Adapter.getAdapter().getConfig().getBoolean("disableProxyIntegration");
	}

	public static void foundCompanion(int protocolVersion) {
		foundCompanion = true;

		int ourProtocolVersion = NegativityMessagesManager.PROTOCOL_VERSION;
		Adapter ada = Adapter.getAdapter();
		if (ada.getConfig().getBoolean("disableProxyIntegration")) {
			ada.getLogger().info("Proxy companion plugin found, but is forcibly disabled.");
		} else if (protocolVersion != ourProtocolVersion) {
			protocolVersionMismatch = true;
			ada.getLogger().info("Proxy companion plugin found, but its protocol version (" + protocolVersion + ") is not the same a ours (" + ourProtocolVersion + ") so it won't be used.");
		} else {
			ada.getLogger().info("Proxy companion plugin found, it will be used.");
		}
	}
}
