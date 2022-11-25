package com.elikill58.negativity.api.entity;

public abstract class AbstractEntity implements Entity {
	
	
	
	@Override
	public String toString() {
		return "Entity{id=" + getEntityId() + ",type=" + getType().name() + ",location=" + getLocation() + "}";
	}
}
