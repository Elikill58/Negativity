package com.elikill58.negativity.minestom.impl.entity;

import java.util.UUID;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.OfflinePlayer;

import net.minecraft.entity.player.PlayerEntity;

public class MinestomOfflinePlayer extends AbstractEntity implements OfflinePlayer {

	private final PlayerEntity u;
	
	public MinestomOfflinePlayer(PlayerEntity u) {
		this.u = u;
	}

	@Override
	public UUID getUniqueId() {
		return u.getUuid();
	}
	
	@Override
	public boolean isDead() {
		return !u.isAlive();
	}

	@Override
	public boolean isOnline() {
		return u.isAlive();
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isOp() {
		return u.isCreativeLevelTwoOp();
	}

	@Override
	public String getName() {
		return u.getName().getString();
	}

	@Override
	public Object getDefault() {
		return u;
	}
	
	@Override
	public String getEntityId() {
		return String.valueOf(u.getId());
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}
}
