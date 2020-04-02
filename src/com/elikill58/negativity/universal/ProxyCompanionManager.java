package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;

public class ProxyCompanionManager {

	public static boolean searchedCompanion = false;
	public static boolean foundCompanion = false;
	public static boolean protocolVersionMismatch = true;
	public static boolean forceDisabled = false;

	public static boolean isIntegrationEnabled() {
		return foundCompanion && !forceDisabled && !protocolVersionMismatch;
	}

	public static void updateForceDisabled(boolean forceDisabled) {
		ProxyCompanionManager.forceDisabled = forceDisabled;
		// Allows to change forceDisabled and ping again without restarting the server.
		// Callers should send a ping ASAP to make sure the integration is not disabled.
		searchedCompanion = false;
		foundCompanion = false;
		protocolVersionMismatch = false;
	}

	public static void foundCompanion(int protocolVersion) {
		foundCompanion = true;

		int ourProtocolVersion = NegativityMessagesManager.PROTOCOL_VERSION;
		if (forceDisabled) {
			Adapter.getAdapter().log("Proxy companion plugin found, but is forcibly disabled.");
		} else if (protocolVersion != ourProtocolVersion) {
			protocolVersionMismatch = true;
			Adapter.getAdapter().log("Proxy companion plugin found, but its protocol version (" + protocolVersion + ") is not the same a ours (" + ourProtocolVersion + ") so it won't be used.");
		} else {
			Adapter.getAdapter().log("Proxy companion plugin found, it will be used.");
		}
	}
}
