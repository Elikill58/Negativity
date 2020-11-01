package com.elikill58.negativity.universal.setBack;

import com.elikill58.negativity.api.entity.Player;

public interface SetBackProcessor {

	/**
	 * Get the name of the setback processor.
	 * 
	 * @return processor name
	 */
	public String getName();
	
	/**
	 * Perform set back to player.
	 * Don't check if the player can receive it, just do it.
	 * 
	 * @param p the player which will have to be setted back
	 * @param c the cheat which the player have been detected
	 */
	public void perform(Player p);
}
