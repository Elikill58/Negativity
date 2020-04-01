package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.adapter.Adapter;

public class ProxyCompanionManager {

	public static boolean searchedCompanion = false;
	public static boolean foundCompanion = false;
	public static boolean forceDisabled = false;

	public static boolean isIntegrationEnabled() {
		return foundCompanion && !forceDisabled;
	}

	public static void updateForceDisabled(boolean forceDisabled) {
		ProxyCompanionManager.forceDisabled = forceDisabled;
		// Allows to change forceDisabled and ping again without restarting the server.
		// Callers should send a ping ASAP to make sure the integration is not disabled.
		searchedCompanion = false;
		foundCompanion = false;
	}

	public static void foundCompanion() {
		foundCompanion = true;
		if (ProxyCompanionManager.forceDisabled) {
			Adapter.getAdapter().log("Proxy companion plugin found, but is forcibly disabled.");
		} else {
			Adapter.getAdapter().log("Proxy companion plugin found, it will be used.");
		}
	}
}
