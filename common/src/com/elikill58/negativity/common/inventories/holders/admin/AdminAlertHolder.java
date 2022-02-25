package com.elikill58.negativity.common.inventories.holders.admin;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.universal.alerts.AlertSender;

public class AdminAlertHolder extends NegativityHolder {

	private final HashMap<Integer, AlertSender> alertSender = new HashMap<>();

	public AlertSender getAlertSender(int i) {
		return alertSender.get(i);
	}
	
	public void add(int slot, AlertSender as) {
		alertSender.put(slot, as);
	}
}
