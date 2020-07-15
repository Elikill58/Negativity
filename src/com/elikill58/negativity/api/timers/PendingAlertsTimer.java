package com.elikill58.negativity.api.timers;

import java.util.ArrayList;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.IPlayerCheatAlertEvent;
import com.elikill58.negativity.universal.Negativity;

public class PendingAlertsTimer implements Runnable {

	@Override
	public void run() {
		NegativityPlayer.getAllPlayers().forEach((uuid, np) -> {
			for(IPlayerCheatAlertEvent alert : new ArrayList<>(np.getAlertForAllCheat()))
				Negativity.sendAlertMessage(np, alert);
			np.saveProof();
		});
	}

}
