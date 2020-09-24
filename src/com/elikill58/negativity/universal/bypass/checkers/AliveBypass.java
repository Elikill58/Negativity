package com.elikill58.negativity.universal.bypass.checkers;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.BypassChecker;

public class AliveBypass implements BypassChecker {

	@Override
	public boolean hasBypass(Player p, Cheat c) {
		return p.isDead() || p.getHealth() == 0.0;
	}
}
