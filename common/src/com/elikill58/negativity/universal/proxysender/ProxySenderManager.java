package com.elikill58.negativity.universal.proxysender;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.proxysender.hook.DefaultProxySender;

public class ProxySenderManager {

	private static ProxySender proxySender = new DefaultProxySender();
	public static ProxySender getProxySender() {
		return proxySender;
	}
	
	public static void setProxySender(ProxySender proxySender) {
		ProxySenderManager.proxySender = proxySender;
	}

	public static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount) {
		getProxySender().sendAlertMessage(p, cheatName, reliability, ping, hover, alertsCount);
	}
}
