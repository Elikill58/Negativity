package com.elikill58.negativity.spigot.impl.entity;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;

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
	
	@Override
	public BoundingBox getBoundingBox() {
		try {
			Object bb = PacketUtils.getBoundingBox(entity);
			Class<?> clss = bb.getClass();
			if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
				double minX = clss.getField("minX").getDouble(bb);
				double minY = clss.getField("minY").getDouble(bb);
				double minZ = clss.getField("minZ").getDouble(bb);
				
				double maxX = clss.getField("maxX").getDouble(bb);
				double maxY = clss.getField("maxY").getDouble(bb);
				double maxZ = clss.getField("maxZ").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			} else {
				double minX = clss.getField("a").getDouble(bb);
				double minY = clss.getField("b").getDouble(bb);
				double minZ = clss.getField("c").getDouble(bb);
				
				double maxX = clss.getField("d").getDouble(bb);
				double maxY = clss.getField("e").getDouble(bb);
				double maxZ = clss.getField("f").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
