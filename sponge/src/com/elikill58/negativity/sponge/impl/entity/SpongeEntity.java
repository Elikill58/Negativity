package com.elikill58.negativity.sponge.impl.entity;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SpongeEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	private final Location loc;
	private @Nullable EntityType cachedEntityType;
	
	public SpongeEntity(E e) {
		this.entity = e;
		this.loc = LocationUtils.toNegativity(e.serverLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.require(Keys.ON_GROUND);
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
		return entity.require(Keys.EYE_HEIGHT);
	}

	@Override
	public EntityType getType() {
		if (this.cachedEntityType == null) {
			ResourceKey key = Utils.getKey(this.entity.type());
			this.cachedEntityType = EntityType.get(key.value().toUpperCase(Locale.ROOT)); // TODO implement this properly using real minecraft IDs
		}
		return this.cachedEntityType;
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
		return entity.get(Keys.DISPLAY_NAME).map(component -> PlainTextComponentSerializer.plainText().serialize(component))
			.orElseGet(() -> Utils.getKey(entity.type()).value());
	}
	
	@Override
	public Location getEyeLocation() {
		return entity.get(Keys.EYE_POSITION)
			.map(vec -> LocationUtils.toNegativity((ServerWorld) entity.world(), vec))
			.orElseGet(() -> LocationUtils.toNegativity(entity.serverLocation()));
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = entity.direction();
		return new Vector(vec.x(), vec.y(), vec.z());
	}
	
	@Override
	public String getEntityId() {
		return entity.uniqueId().toString();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		AABB box = entity.boundingBox().orElse(null);
		if(box == null)
			return null;
		Vector3d min = box.min(), max = box.max();
		return new BoundingBox(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
	}

	@Override
	public Vector getTheoricVelocity() {
		Vector3d vec = entity.getOrElse(Keys.VELOCITY, new Vector3d(0, 0, 0));
		return new Vector(vec.x(), vec.y(), vec.z());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.offer(Keys.VELOCITY, new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
}
