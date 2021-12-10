package com.elikill58.negativity.universal.proxysender.hook;

import java.io.IOException;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.proxysender.ProxySender;

public class DefaultProxySender implements ProxySender {

	@Override
	public void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover,
			int alertsCount) {
		try {
			AlertMessage alertMessage = new AlertMessage(p.getName(), cheatName, reliability, ping, hover, alertsCount);
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID,
					NegativityMessagesManager.writeMessage(alertMessage));
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not send alert message to the proxy.");
			e.printStackTrace();
		}
	}

}
