package com.elikill58.negativity.minestom.impl.entity;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.OfflinePlayer;

public class MinestomOfflinePlayer extends AbstractEntity implements OfflinePlayer {

	private final UUID uuid;
	private final String name;
	
	public MinestomOfflinePlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public @Nullable Object getDefault() {
		return null;
	}
}
