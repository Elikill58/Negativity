package com.elikill58.negativity.common.events.entity;

import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.events.Event;

public class EntityShootBowEvent implements Event {
	
	private final Entity et;
	
	public EntityShootBowEvent(Entity shoot) {
		this.et = shoot;
	}
	
	public Entity getEntity() {
		return et;
	}
}
