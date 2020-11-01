package com.elikill58.negativity.api.events.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.events.Event;

public class EntityShootBowEvent implements Event {
	
	private final Entity et;
	
	public EntityShootBowEvent(Entity shoot) {
		this.et = shoot;
	}
	
	public Entity getEntity() {
		return et;
	}
}
