package com.elikill58.negativity.api.entity;

import java.util.UUID;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;

public interface OfflinePlayer extends Entity {

	UUID getUniqueId();
	
	boolean isOnline();
	
	boolean hasPlayedBefore();
	
	@Override
	default World getWorld() {
		return null;
	}
	
	@Override
	default boolean isDead() {
		return true;
	}
	
	@Override
	default boolean isOnGround() {
		return true;
	}
	
	@Override
	default Location getLocation() {
		return null;
	}
	
	@Override
	default EntityType getType() {
		return EntityType.PLAYER;
	}
	
	@Override
	default void sendMessage(String msg) {}
	
	@Override
	default BoundingBox getBoundingBox() {
		return null;
	}
	
	@Override
	default String getEntityId() {
		return null;
	}
	
	@Override
	default double getEyeHeight() {
		return 0;
	}
}
