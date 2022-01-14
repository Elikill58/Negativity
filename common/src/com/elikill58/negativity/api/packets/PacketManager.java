package com.elikill58.negativity.api.packets;

import com.elikill58.negativity.api.entity.Player;

public abstract class PacketManager {

	public void load() {}
	
	/**
	 * Add player to analyzed one
	 * 
	 * @param p the player added to packet listener
	 */
	public void addPlayer(Player p) {}
	
	/**
	 * Remove player to analyzed one
	 * 
	 * @param p the player removed to packet listener
	 */
	public void removePlayer(Player p) {}
	
	/**
	 * Remove all player in current listener
	 */
	public void clear() {}
}
