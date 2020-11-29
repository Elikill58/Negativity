package com.elikill58.negativity.api.entity;

public interface Arrow extends Entity {
	
	/**
	 * Get the entity which shoot the arrow
	 * 
	 * @return entity which shoot
	 */
	Entity getShooter();
}
