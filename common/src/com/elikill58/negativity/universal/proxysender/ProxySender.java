package com.elikill58.negativity.universal.proxysender;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat.CheatHover;

public interface ProxySender {
	
	/**
	 * Send the alert to ALL proxy.
	 * 
	 * @param p the player used to send informations
	 * @param cheatName the name of the cheat
	 * @param reliability the reliability of alert
	 * @param ping the ping of player
	 * @param hover the cheat hover message
	 * @param alertsCount the alert amount
	 */
	void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount);

}
