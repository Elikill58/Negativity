package com.elikill58.negativity.sponge7.impl.entity;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.User;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.OfflinePlayer;

public class SpongeOfflinePlayer extends AbstractEntity implements OfflinePlayer {

	private final User u;
	
	public SpongeOfflinePlayer(User u) {
		this.u = u;
	}

	@Override
	public UUID getUniqueId() {
		return u.getUniqueId();
	}

	@Override
	public boolean isOnline() {
		return u.isOnline();
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isOp() {
		return u.hasPermission("*");
	}

	@Override
	public String getName() {
		return u.getName();
	}

	@Override
	public Object getDefault() {
		return u;
	}
	
	@Override
	public String getEntityId() {
		return u.getIdentifier();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}
}
