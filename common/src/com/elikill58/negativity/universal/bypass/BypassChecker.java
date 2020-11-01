package com.elikill58.negativity.universal.bypass;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat;

public interface BypassChecker {

	/**
	 * Checker if the player has bypass
	 * 
	 * @param p the player which we are looking for bypass
	 * @param c the cheat that we are checking
	 * @return true if the player have bypass
	 */
	public boolean hasBypass(Player p, Cheat c);
}
