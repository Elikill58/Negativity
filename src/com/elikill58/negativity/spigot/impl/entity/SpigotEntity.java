package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

public class SpigotEntity extends Entity {

	private final org.bukkit.entity.Entity entity;
	private final SpigotLocation loc;
	
	public SpigotEntity(org.bukkit.entity.Entity entity) {
		this.entity = entity;
		this.loc = new SpigotLocation(entity.getLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isOp() {
		return entity.isOp();
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public double getEyeHeight() {
		return entity.getHeight();
	}
}
