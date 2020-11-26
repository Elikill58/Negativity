package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class SpongeEntity extends Entity {

	private final org.spongepowered.api.entity.Entity entity;
	private final SpongeLocation loc;
	
	public SpongeEntity(org.spongepowered.api.entity.Entity e) {
		this.entity = e;
		this.loc = new SpongeLocation(e.getServerLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.require(Keys.ON_GROUND);
	}

	@Override
	public boolean isOp() {
		return true;
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
	public Object getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		
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
