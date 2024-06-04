package com.elikill58.negativity.minestom.impl.entity;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;

import net.minestom.server.entity.metadata.projectile.ArrowMeta;

public class MinestomArrow extends MinestomEntity<net.minestom.server.entity.Entity> implements Arrow {

	public MinestomArrow(net.minestom.server.entity.Entity arrow) {
		super(arrow);
	}
	
	@Override
	public Entity getShooter() {
		return MinestomEntityManager.getEntity(((ArrowMeta) entity.getEntityMeta()).getShooter());
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}
}
