package com.elikill58.negativity.spigot.impl.entity;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;

public class SpigotEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	
	public SpigotEntity(E entity) {
		this.entity = entity;
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
		return new SpigotLocation(entity.getLocation());
	}

	@Override
	public double getEyeHeight() {
		return entity.getHeight();
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getType().name());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		if(entity instanceof CommandSender)
			((CommandSender) entity).sendMessage(msg);
	}

	@Override
	public String getName() {
		if(entity instanceof HumanEntity) // prevent 1.7 error
			return ((HumanEntity) entity).getName();
		return entity.getName();
	}
	
	@Override
	public Location getEyeLocation() {
		if(entity instanceof LivingEntity) {
			org.bukkit.Location eye = ((LivingEntity) entity).getEyeLocation();
			return new SpigotLocation(new SpigotWorld(eye.getWorld()), eye.getX(), eye.getY(), eye.getZ());
		}
		return null;
	}
	
	@Override
	public Vector getRotation() {
		org.bukkit.util.Vector vec = entity.getLocation().getDirection();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return entity.getEntityId();
	}
}
