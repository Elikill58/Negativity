package com.elikill58.negativity.universal.alerts.hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;

public class TimeAlertSender extends AlertSender {

	private ScheduledTask task;
	private int time = 1000;
	
	public TimeAlertSender() {
		super("time", true);
	}
	
	@Override
	public int getDefaultValue() {
		return 1000;
	}
	
	@Override
	public void addOne() {
		time += 1000;
	}
	
	@Override
	public void removeOne() {
		time -= 1000;
	}
	
	@Override
	public int getValue() {
		return time;
	}
	
	@Override
	public String getShowedValue() {
		return (int) (getValue() / 1000) + "s";
	}
	
	@Override
	public void stop() {
		if(task != null)
			task.cancel();
		task = null;
	}
	
	@Override
	public void config(Configuration config) {
		stop();
		time = config.getInt("value", getDefaultValue());
		task = Adapter.getAdapter().getScheduler().runRepeating(() -> {
			new HashMap<>(NegativityPlayer.getAllPlayers()).forEach((uuid, np) -> {
				for(PlayerCheatAlertEvent alert : new ArrayList<>(np.getAlertForAllCheat()))
					Negativity.sendAlertMessage(np, alert);
			});
		}, (time * 20) / 1000);
	}
	
	@Override
	public void save() {
		Adapter ada = Adapter.getAdapter();
		Configuration config = ada.getConfig();
		config.set("alert.show.type", name);
		config.set("alert.show.value", time);
		config.save();
	}
	
	@Override
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		CheatKeys cheatKey = alert.getCheat().getKey();
		List<PlayerCheatAlertEvent> tempList = np.alertNotShowed.computeIfAbsent(cheatKey, (a) -> new ArrayList<>());
		tempList.add(alert);
		np.alertNotShowed.put(cheatKey, tempList);
	}

}
