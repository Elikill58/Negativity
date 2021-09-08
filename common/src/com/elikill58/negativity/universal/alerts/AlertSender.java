package com.elikill58.negativity.universal.alerts;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.config.Configuration;

public interface AlertSender {

	public String getName();
	
	public boolean canChangeDefaultValue();
	
	public default int getDefaultValue() {
		return 0;
	}

	public default int addOne() {
		return 0;
	}
	public default int removeOne() {
		return 0;
	}
	
	public default String getShowedValue() {
		return "none";
	}
	
	public default int getValue() {
		return 0;
	}
	
	public default void config(Configuration config) {}
	
	public default void stop() {}
	
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert);
}
