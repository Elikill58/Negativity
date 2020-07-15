package com.elikill58.negativity.common.events.entity;

import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.events.Event;

public class ProjectileHitEvent implements Event {
	
	private final Entity entity;
	
	public ProjectileHitEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
