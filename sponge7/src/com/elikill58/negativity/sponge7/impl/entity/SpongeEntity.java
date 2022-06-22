package com.elikill58.negativity.sponge7.impl.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.util.AABB;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge7.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge7.impl.location.SpongeWorld;
import com.flowpowered.math.vector.Vector3d;

public class SpongeEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	private final Location loc;
	
	public SpongeEntity(E e) {
		this.entity = e;
		this.loc = SpongeLocation.toCommon(e.getLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}
	
	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
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
		// TODO implement getEyeHeight
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getType().getId());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		if (entity instanceof MessageReceiver) {
			((MessageReceiver) entity).sendMessage(Text.of(msg));
		}
	}

	@Override
	public String getName() {
		return entity.get(Keys.DISPLAY_NAME).orElse(Text.of(entity.getType().getName())).toPlain();
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = entity.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(entity.getRotation());
		return new Location(new SpongeWorld(entity.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = entity.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public Vector getTheoricVelocity() {
		Vector3d vel = entity.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public String getEntityId() {
		return entity.getUniqueId().toString();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		AABB box = entity.getBoundingBox().orElse(null);
		if(box == null)
			return null;
		Vector3d min = box.getMin(), max = box.getMax();
		return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}
}
