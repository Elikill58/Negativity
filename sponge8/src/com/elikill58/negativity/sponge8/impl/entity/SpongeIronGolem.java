package com.elikill58.negativity.sponge8.impl.entity;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge8.utils.Utils;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class SpongeIronGolem extends IronGolem {

	private final org.spongepowered.api.entity.living.golem.IronGolem golem;
	
	public SpongeIronGolem(org.spongepowered.api.entity.living.golem.IronGolem golem) {
		this.golem = golem;
	}
	
	@Override
	public boolean isOnGround() {
		return golem.require(Keys.ON_GROUND);
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(golem.getServerLocation());
	}

	@Override
	public double getEyeHeight() {
		return Utils.getEntityHeadHeight(golem);
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}

	@Override
	public Entity getTarget() {
		return SpongeEntityManager.getEntity(golem.getOrNull(Keys.TARGET_ENTITY));
	}

	@Override
	public Object getDefault() {
		return golem;
	}

	@Override
	public void sendMessage(String msg) {
		
	}

	@Override
	public String getName() {
		return golem.get(Keys.DISPLAY_NAME).map(component -> PlainComponentSerializer.plain().serialize(component))
			.orElse("Golem");
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = golem.require(Keys.EYE_POSITION);
		return new SpongeLocation((ServerWorld) golem.getWorld(), vec);
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = golem.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
}
