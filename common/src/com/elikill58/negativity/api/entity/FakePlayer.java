package com.elikill58.negativity.api.entity;

import java.util.UUID;

public abstract class FakePlayer extends Entity {


	/**
	 * Show the fake player to the specified online player
	 * 
	 * @param p THe player who will see the entity
	 */
	public abstract void show(Player p);

	/**
	 * Hide the fake player to the specified online player
	 * 
	 * @param p The player that will not see it
	 */
	public abstract void hide(Player p);


	/**
	 *  Get the entity ID of the fake player.
	 *  Alone method to check entity
	 * 
	 * @return the entity ID
	 */
	public abstract int getEntityId();
	
	/**
	 * Get Unique ID of the fake player
	 * 
	 * @return the player's uuid
	 */
	public abstract UUID getUUID();
	
	@Override
	public void sendMessage(String msg) {}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
}
