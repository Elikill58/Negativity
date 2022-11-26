package com.elikill58.negativity.api.entity;

import com.elikill58.negativity.universal.utils.ChatUtils;

public abstract class AbstractEntity implements Entity {
	
	@Override
	public String getName() {
		return ChatUtils.capitalize(getType().name());
	}
	
	@Override
	public String toString() {
		return "Entity{id=" + getEntityId() + ",type=" + getType().name() + ",location=" + getLocation() + "}";
	}
}
