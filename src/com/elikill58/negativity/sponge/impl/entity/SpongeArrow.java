package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.utils.Utils;

public class SpongeArrow extends Arrow {

	private final org.spongepowered.api.entity.projectile.arrow.Arrow arrow;
	
	public SpongeArrow(org.spongepowered.api.entity.projectile.arrow.Arrow arrow) {
		this.arrow = arrow;
	}
	
	@Override
	public Entity getShooter() {
		return SpongeEntityManager.getProjectile(arrow.getShooter());
	}

	@Override
	public boolean isOnGround() {
		return arrow.isOnGround();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(arrow.getLocation());
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
		return arrow.get(Keys.DISPLAY_NAME).orElse(Text.of("Arrow")).toPlain();
	}

	@Override
	public Object getDefault() {
		return arrow;
	}
	
}
