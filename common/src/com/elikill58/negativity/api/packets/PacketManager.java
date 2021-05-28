package com.elikill58.negativity.api.packets;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;

public abstract class PacketManager {

	/**
	 * Add player to analyzed one
	 * 
	 * @param p the player added to packet listener
	 */
	public abstract void addPlayer(Player p);
	
	/**
	 * Remove player to analyzed one
	 * 
	 * @param p the player removed to packet listener
	 */
	public abstract void removePlayer(Player p);
	
	/**
	 * Remove all player in current listener
	 */
	public abstract void clear();

	protected final List<PacketHandler> handlers = new ArrayList<>();
	
	/**
	 * Add handler at current packet manager
	 * 
	 * @param handler the new handler
	 * @return true if the handler have been added
	 */
	public boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	/**
	 * Remove handler at current packet manager
	 * 
	 * @param handler the removed handler
	 * @return true if the handler was existing
	 */
	public boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
}
