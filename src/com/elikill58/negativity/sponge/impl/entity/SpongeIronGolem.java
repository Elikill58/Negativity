package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.IronGolem;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge.utils.Utils;
import com.flowpowered.math.vector.Vector3d;

public class SpongeIronGolem extends IronGolem {

	private final org.spongepowered.api.entity.living.golem.IronGolem golem;
	
	public SpongeIronGolem(org.spongepowered.api.entity.living.golem.IronGolem golem) {
		this.golem = golem;
	}
	
	@Override
	public boolean isOnGround() {
		return golem.isOnGround();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(golem.getLocation());
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
		return SpongeEntityManager.getEntity(golem.getTarget().orElse(null));
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
		return golem.get(Keys.DISPLAY_NAME).orElse(Text.of("Golem")).toPlain();
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = golem.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(golem.getRotation());
		return new SpongeLocation(new SpongeWorld(golem.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
}
