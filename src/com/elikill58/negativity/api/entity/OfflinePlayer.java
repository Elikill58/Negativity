package com.elikill58.negativity.api.entity;

import java.util.UUID;

public abstract class OfflinePlayer extends Entity {

	public abstract UUID getUniqueId();
	
	public abstract boolean isOnline();
	
	public abstract boolean hasPlayedBefore();
}
