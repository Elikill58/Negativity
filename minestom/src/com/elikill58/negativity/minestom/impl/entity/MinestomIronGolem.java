package com.elikill58.negativity.minestom.impl.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;

public class MinestomIronGolem extends MinestomEntity<net.minestom.server.entity.Entity> implements IronGolem {
	
	public MinestomIronGolem(net.minestom.server.entity.Entity golem) {
		super(golem);
	}
	
	@Override
	public Entity getTarget() {
		return null; // TODO implement golem target
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}
}
