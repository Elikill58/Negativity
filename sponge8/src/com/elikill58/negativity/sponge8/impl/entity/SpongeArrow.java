package com.elikill58.negativity.sponge8.impl.entity;

import org.spongepowered.api.data.Keys;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge8.utils.Utils;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class SpongeArrow extends Arrow {

	private final org.spongepowered.api.entity.projectile.arrow.Arrow arrow;
	
	public SpongeArrow(org.spongepowered.api.entity.projectile.arrow.Arrow arrow) {
		this.arrow = arrow;
	}
	
	@Override
	public Entity getShooter() {
		return SpongeEntityManager.getProjectile(arrow.require(Keys.SHOOTER));
	}

	@Override
	public boolean isOnGround() {
		return arrow.require(Keys.ON_GROUND);
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(arrow.getServerLocation());
	}

	@Override
	public double getEyeHeight() {
		return Utils.getEntityHeadHeight(arrow);
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}

	@Override
	public void sendMessage(String msg) {
		
	}

	@Override
	public String getName() {
		return arrow.get(Keys.DISPLAY_NAME).map(component -> PlainComponentSerializer.plain().serialize(component))
			.orElse("Arrow");
	}

	@Override
	public Object getDefault() {
		return arrow;
	}
	
	@Override
	public Location getEyeLocation() {
		return new SpongeLocation(arrow.getServerLocation());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = arrow.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
}
