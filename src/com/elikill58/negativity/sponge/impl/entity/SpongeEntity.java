package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;

public class SpongeEntity extends Entity {

	private final org.spongepowered.api.entity.Entity entity;
	private final SpongeLocation loc;
	
	public SpongeEntity(org.spongepowered.api.entity.Entity e) {
		this.entity = e;
		this.loc = new SpongeLocation(e.getLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
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
		return EntityType.get(entity == null ? null : entity.getType().getId());
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
		return entity.get(Keys.DISPLAY_NAME).orElse(Text.of(entity.getType().getName())).toPlain();
	}
}
