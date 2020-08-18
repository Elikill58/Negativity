package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

public class SpigotArrow extends Arrow {

	private final org.bukkit.entity.Arrow arrow;
	
	public SpigotArrow(org.bukkit.entity.Arrow arrow) {
		this.arrow = arrow;
	}
	
	@Override
	public Entity getShooter() {
		return SpigotEntityManager.getProjectile(arrow.getShooter());
	}

	@Override
	public boolean isOnGround() {
		return arrow.isOnGround();
	}

	@Override
	public boolean isOp() {
		return arrow.isOp();
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(arrow.getLocation());
	}

	@Override
	public double getEyeHeight() {
		return arrow.getHeight();
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}

	@Override
	public int getEntityId() {
		return arrow.getEntityId();
	}

	@Override
	public void sendMessage(String msg) {
		arrow.sendMessage(msg);
	}

	@Override
	public String getName() {
		return arrow.getName();
	}

	@Override
	public Object getDefault() {
		return arrow;
	}
	
}
