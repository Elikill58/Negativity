package com.elikill58.negativity.universal.alerts.hook;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.alerts.AlertSender;

public class InstantAlertSender extends AlertSender {

	public InstantAlertSender() {
		super("instant", false);
	}
	
	@Override
	public void save() {
		Adapter ada = Adapter.getAdapter();
		Configuration config = ada.getConfig();
		config.set("alert.show.type", name);
		config.save();
	}

	@Override
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		Negativity.sendAlertMessage(np, alert);
	}

}
