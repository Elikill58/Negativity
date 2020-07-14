package com.elikill58.negativity.common.timers;

import java.util.ArrayList;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.events.negativity.IPlayerCheatAlertEvent;
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
