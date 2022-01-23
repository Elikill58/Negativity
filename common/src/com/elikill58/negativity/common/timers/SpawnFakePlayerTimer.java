package com.elikill58.negativity.common.timers;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.keys.CheatKeys;

public class SpawnFakePlayerTimer implements Runnable {

	@Override
	public void run() {
		if(Cheat.forKey(CheatKeys.FORCEFIELD).isActive())
			return;
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			NegativityPlayer.getNegativityPlayer(p).makeAppearEntities();
		}
	}

}
