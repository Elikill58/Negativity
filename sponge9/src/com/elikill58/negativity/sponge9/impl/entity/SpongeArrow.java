package com.elikill58.negativity.sponge9.impl.entity;

import org.spongepowered.api.data.Keys;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;

public class SpongeArrow extends SpongeEntity<org.spongepowered.api.entity.projectile.arrow.Arrow> implements Arrow {
	
	public SpongeArrow(org.spongepowered.api.entity.projectile.arrow.Arrow arrow) {
		super(arrow);
	}
	
	@Override
	public Entity getShooter() {
		return SpongeEntityManager.getProjectile(entity.require(Keys.SHOOTER));
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}
}
