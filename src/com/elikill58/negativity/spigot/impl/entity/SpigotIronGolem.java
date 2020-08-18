package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;
import com.elikill58.negativity.api.location.Location;
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
		return golem.getTarget() == null ? null : SpigotEntityManager.getEntity(golem.getTarget());
	}

	@Override
	public Object getDefault() {
		return golem;
	}

	@Override
	public void sendMessage(String msg) {
		golem.sendMessage(msg);
	}

	@Override
	public String getName() {
		return golem.getName();
	}

}
