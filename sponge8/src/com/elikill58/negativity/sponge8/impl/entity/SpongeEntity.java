package com.elikill58.negativity.sponge8.impl.entity;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class SpongeEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	private final SpongeLocation loc;
	
	public SpongeEntity(E e) {
		this.entity = e;
		this.loc = new SpongeLocation(e.getServerLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.require(Keys.ON_GROUND);
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
	public double getEyeHeight() {
		// TODO implement getEyeHeight
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity.getType().key().value()); // TODO implement this properly using real minecraft IDs
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		if (entity instanceof Audience) {
			((Audience) entity).sendMessage(Component.text(msg));
		}
	}

	@Override
	public String getName() {
		return entity.get(Keys.DISPLAY_NAME).map(component -> PlainComponentSerializer.plain().serialize(component))
			.orElseGet(() -> entity.getType().key().asString());
	}
	
	@Override
	public Location getEyeLocation() {
		return entity.get(Keys.EYE_POSITION)
			.map(vec -> new SpongeLocation((ServerWorld) entity.getWorld(), vec))
			.orElseGet(() -> new SpongeLocation(entity.getServerLocation()));
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = entity.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
}
