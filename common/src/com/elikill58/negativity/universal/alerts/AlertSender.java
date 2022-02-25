package com.elikill58.negativity.universal.alerts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.alerts.hook.AmountAlertSender;
import com.elikill58.negativity.universal.alerts.hook.InstantAlertSender;
import com.elikill58.negativity.universal.alerts.hook.TimeAlertSender;

public abstract class AlertSender {

	protected final String name;
	protected final boolean changeDefaultValue;
	
	public AlertSender(String name, boolean changeDefaultValue) {
		this.name = name;
		this.changeDefaultValue = changeDefaultValue;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean canChangeDefaultValue() {
		return changeDefaultValue;
	}
	
	public int getDefaultValue() {
		return 0;
	}

	public void addOne() {}
	public void removeOne() {}
	
	public String getShowedValue() {
		return "none";
	}
	
	public int getValue() {
		return 0;
	}
	
	public void save() {}
	
	public void config(Configuration config) {}
	
	public void stop() {}
	
	public abstract void alert(NegativityPlayer np, PlayerCheatAlertEvent alert);

	private static AlertSender alertSender;
	private static List<AlertSender> allAlertSender = new ArrayList<>();
	
	public static AlertSender getAlertShower() {
		return alertSender;
	}
	
	public static AlertSender getAlertShowerOfTypeName(String type) {
		return allAlertSender.stream().filter(sender -> sender.getName().equalsIgnoreCase(type)).findFirst().orElse(new TimeAlertSender());
	}
	
	public static void initAlertShower(Adapter ada) {
		allAlertSender.addAll(Arrays.asList(new InstantAlertSender(), new AmountAlertSender(), new TimeAlertSender()));
		
		Configuration config = ada.getConfig().getSection("alert.show");

		String type = config.getString("type", "time");
		alertSender = getAlertShowerOfTypeName(type);
		alertSender.config(config);
	}
	
	public static void refreshAlertShower(Adapter ada, AlertSender newShower) {
		if(alertSender != null)
			alertSender.stop();
		Configuration config = ada.getConfig().getSection("alert.show");

		alertSender = newShower;
		alertSender.config(config);
	}
	
	public static void setAlertShower(String type) {
		setAlertShower(getAlertShowerOfTypeName(type));
	}
	
	public static void setAlertShower(AlertSender shower) {
		shower.save();
		refreshAlertShower(Adapter.getAdapter(), shower);
	}
	
	public static List<AlertSender> getAllAlertSender() {
		return allAlertSender;
	}
}
