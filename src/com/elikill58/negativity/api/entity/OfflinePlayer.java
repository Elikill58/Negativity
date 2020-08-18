package com.elikill58.negativity.api.entity;

import java.util.UUID;

import com.elikill58.negativity.api.location.Location;

public abstract class OfflinePlayer extends Entity {

	public abstract UUID getUniqueId();
	
	public abstract boolean isOnline();
	
	public abstract boolean hasPlayedBefore();
	
	@Override
	public boolean isOnGround() {
		return true;
	}
	
	@Override
	public Location getLocation() {
		return null;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
}
