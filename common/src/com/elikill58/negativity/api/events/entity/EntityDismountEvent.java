package com.elikill58.negativity.api.events.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.events.Event;

public class EntityDismountEvent implements Event {
	
	private final Entity entity, dismounted;
	
	public EntityDismountEvent(Entity entity, Entity dismounted) {
		this.entity = entity;
		this.dismounted = dismounted;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Entity getDismounted() {
		return dismounted;
	}
}
