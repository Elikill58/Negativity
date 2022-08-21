package com.elikill58.negativity.minestom.impl.entity;

import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.minestom.impl.location.MinestomLocation;
import com.elikill58.negativity.minestom.impl.location.MinestomWorld;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;

public class MinestomEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	
	public MinestomEntity(E e) {
		this.entity = e;
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}
	
	@Override
	public boolean isDead() {
		return !entity.isActive();
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public Location getLocation() {
		return MinestomLocation.toCommon(entity.getInstance(), entity.getPosition());
	}
	
	@Override
	public World getWorld() {
		return World.getWorld(entity.getInstance().getUniqueId().toString(), a -> new MinestomWorld(entity.getInstance()));
	}

	@Override
	public double getEyeHeight() {
		return entity.getEyeHeight();
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getEntityType().name());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public String getName() {
		return entity.getCustomName().examinableName();
	}
	
	@Override
	public Location getEyeLocation() {
		return getLocation(); // default entity doesn't have eye location
	}
	
	@Override
	public Vector getRotation() {
		return new Vector(0, 0, 0);
	}

	@Override
	public Vector getTheoricVelocity() {
		Vec vel = entity.getVelocity();
		return new Vector(vel.x(), vel.y(), vel.z());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vec(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public String getEntityId() {
		return entity.getUuid().toString();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		net.minestom.server.collision.@NotNull BoundingBox box = entity.getBoundingBox();
		return new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
	}

	@Override
	public void sendMessage(String msg) {
		
	}
}
