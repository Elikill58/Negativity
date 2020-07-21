package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
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

	@Override
	public EntityType getType() {
		return EntityType.get(entity.getType().name());
	}

	@Override
	public int getEntityId() {
		return entity.getEntityId();
	}

	@Override
	public Object getDefaultEntity() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(msg);
	}

	@Override
	public String getName() {
		return entity.getName();
	}
}
