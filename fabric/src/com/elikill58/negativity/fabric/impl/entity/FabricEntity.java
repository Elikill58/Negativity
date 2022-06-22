package com.elikill58.negativity.fabric.impl.entity;

import java.util.Arrays;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.fabric.FabricNegativity;
import com.elikill58.negativity.fabric.impl.location.FabricLocation;
import com.elikill58.negativity.fabric.impl.location.FabricWorld;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class FabricEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	private final Location loc;
	
	public FabricEntity(E e) {
		this.entity = e;
		this.loc = FabricLocation.toCommon(e.getWorld(), e.getPos());
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}
	
	@Override
	public boolean isDead() {
		return !entity.isAlive();
	}

	@Override
	public boolean isOp() {
		return Arrays.asList(FabricNegativity.getInstance().getServer().getPlayerManager().getOpList().getNames()).contains(getName());
	}

	@Override
	public Location getLocation() {
		return loc;
	}
	
	@Override
	public World getWorld() {
		return loc.getWorld();
	}

	@Override
	public double getEyeHeight() {
		return entity.getEyeHeight(entity.getPose());
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getType().getName().asString());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).sendMessage(Text.of(msg), false);
		}
	}

	@Override
	public String getName() {
		return entity.getName().asString();
	}
	
	@Override
	public Location getEyeLocation() {
		Vec3d vec = entity.getEyePos();
		return new Location(new FabricWorld(entity.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vec3d vec = entity.getRotationVector();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public Vector getTheoricVelocity() {
		Vec3d vel = entity.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vec3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public String getEntityId() {
		return entity.getUuid().toString();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		Box box = entity.getBoundingBox();
		if(box == null)
			return null;
		return new BoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}
}
