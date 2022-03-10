package com.elikill58.negativity.universal.alerts.hook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;

public class AmountAlertSender extends AlertSender {

	private int amount = 10;
	
	public AmountAlertSender() {
		super("amount", true);
	}
	
	@Override
	public int getDefaultValue() {
		return 10;
	}
	
	@Override
	public void addOne() {
		amount++;
	}
	
	@Override
	public void removeOne() {
		amount--;
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
	public void save() {
		Adapter ada = Adapter.getAdapter();
		Configuration config = ada.getConfig();
		config.set("alert.show.type", name);
		config.set("alert.show.value", amount);
		config.save();
	}
	
	@Override
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		CheatKeys cheatKey = alert.getCheat().getKey();
		List<PlayerCheatAlertEvent> tempList = np.alertNotShowed.getOrDefault(cheatKey, new ArrayList<>());
		tempList.add(alert);
		PlayerCheatAlertEvent nextAlert = np.getAlertForCheat(alert.getCheat(), tempList);
		if(amount <= nextAlert.getNbAlert() || !(alert.getReportType().equals(ReportType.WARNING) || alert.getReportType().equals(ReportType.VIOLATION))) { // if enough alerts
			Negativity.sendAlertMessage(np, nextAlert);
		} else
			np.alertNotShowed.put(cheatKey, new ArrayList<>(Arrays.asList(nextAlert)));
	}

}
