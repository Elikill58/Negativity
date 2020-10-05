package com.elikill58.negativity.api.entity;

import java.util.UUID;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;

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

	@Override
	public Location getEyeLocation() {
		return null;
	}

	@Override
	public Vector getRotation() {
		return null;
	}

	@Override
	public void sendMessage(String msg) {}
	
	@Override
	public int getEntityId() {
		return 0;
	}
	
	@Override
	public double getEyeHeight() {
		return 0;
	}
}
