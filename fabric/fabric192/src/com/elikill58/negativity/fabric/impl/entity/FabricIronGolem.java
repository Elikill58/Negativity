package com.elikill58.negativity.fabric.impl.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;

import net.minecraft.entity.passive.IronGolemEntity;

public class FabricIronGolem extends FabricEntity<IronGolemEntity> implements IronGolem {
	
	public FabricIronGolem(IronGolemEntity golem) {
		super(golem);
	}
	
	@Override
	public Entity getTarget() {
		return FabricEntityManager.getEntity(entity.getTarget());
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}
}
