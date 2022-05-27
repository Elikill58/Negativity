package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.data.Keys;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;

public class SpongeIronGolem extends SpongeEntity<org.spongepowered.api.entity.living.golem.IronGolem> implements IronGolem {
	
	public SpongeIronGolem(org.spongepowered.api.entity.living.golem.IronGolem golem) {
		super(golem);
	}
	
	@Override
	public Entity getTarget() {
		return SpongeEntityManager.getEntity(entity.getOrNull(Keys.TARGET_ENTITY));
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}
}
