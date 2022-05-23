package com.elikill58.negativity.fabric.impl.entity;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;

import net.minecraft.entity.projectile.ArrowEntity;

public class FabricArrow extends FabricEntity<ArrowEntity> implements Arrow {

	public FabricArrow(ArrowEntity arrow) {
		super(arrow);
	}
	
	@Override
	public Entity getShooter() {
		return FabricEntityManager.getEntity(entity.getOwner());
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}
}
