package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.entity.EntityType;
import com.elikill58.negativity.common.entity.IronGolem;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

public class SpigotIronGolem extends IronGolem {

	private final org.bukkit.entity.IronGolem golem;
	
	public SpigotIronGolem(org.bukkit.entity.IronGolem golem) {
		this.golem = golem;
	}
	
	@Override
	public boolean isOnGround() {
		return golem.isOnGround();
	}

	@Override
	public boolean isOp() {
		return golem.isOp();
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(golem.getLocation());
	}

	@Override
	public double getEyeHeight() {
		return golem.getEyeHeight();
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}

	@Override
	public int getEntityId() {
		return golem.getEntityId();
	}

	@Override
	public Entity getTarget() {
		return SpigotEntityManager.getEntity(golem.getTarget());
	}

	@Override
	public Object getDefaultEntity() {
		return golem;
	}

}
