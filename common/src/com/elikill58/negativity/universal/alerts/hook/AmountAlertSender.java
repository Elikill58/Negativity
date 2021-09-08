package com.elikill58.negativity.universal.alerts.hook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.report.ReportType;

public class AmountAlertSender implements AlertSender {

	private int amount = 10;
	
	@Override
	public String getName() {
		return "amount";
	}
	
	@Override
	public boolean canChangeDefaultValue() {
		return true;
	}
	
	@Override
	public int getDefaultValue() {
		return 10;
	}
	
	@Override
	public int addOne() {
		return amount++;
	}
	
	@Override
	public int removeOne() {
		return amount--;
	}
	
	@Override
	public int getValue() {
		return amount;
	}
	
	@Override
	public String getShowedValue() {
		return String.valueOf(getValue());
	}
	
	@Override
	public void config(Configuration config) {
		amount = config.getInt("value", getDefaultValue());
	}
	
	@Override
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		String cheatKey = alert.getCheat().getKey();
		List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.getOrDefault(cheatKey, new ArrayList<>());
		tempList.add(alert);
		PlayerCheatAlertEvent nextAlert = np.getAlertForCheat(alert.getCheat(), tempList);
		if(amount <= nextAlert.getNbAlert() || !(alert.getReportType().equals(ReportType.WARNING) || alert.getReportType().equals(ReportType.VIOLATION))) { // if enough alerts
			Negativity.sendAlertMessage(np, nextAlert);
		} else
			np.ALERT_NOT_SHOWED.put(cheatKey, new ArrayList<>(Arrays.asList(nextAlert)));
	}

}
