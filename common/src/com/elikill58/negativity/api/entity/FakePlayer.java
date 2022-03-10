package com.elikill58.negativity.api.entity;

import java.util.UUID;

public interface FakePlayer extends Entity {


	/**
	 * Show the fake player to the specified online player
	 * 
	 * @param p THe player who will see the entity
	 */
	void show(Player p);

	/**
	 * Hide the fake player to the specified online player
	 * 
	 * @param p The player that will not see it
	 */
	void hide(Player p);
	
	/**
	 * Get Unique ID of the fake player
	 * 
	 * @return the player's uuid
	 */
	UUID getUUID();
	
	@Override
	default void sendMessage(String msg) {}

	@Override
	default EntityType getType() {
		return EntityType.PLAYER;
	}
}
