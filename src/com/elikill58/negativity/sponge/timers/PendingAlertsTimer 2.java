package com.elikill58.negativity.sponge.timers;

import java.util.ArrayList;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent.Alert;

public class PendingAlertsTimer implements Runnable {

	@Override
	public void run() {
		SpongeNegativityPlayer.getAllPlayers().forEach((uuid, np) -> {
			for(Alert alert : new ArrayList<>(np.getAlertForAllCheat()))
				SpongeNegativity.sendAlertMessage(np, alert);
			np.saveData();
		});
	}
}
