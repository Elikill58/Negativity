package com.elikill58.negativity.spigot.impl.entity;

import java.util.UUID;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.location.Location;

public class SpigotOfflinePlayer extends OfflinePlayer {

	private final org.bukkit.OfflinePlayer op;
	
	public SpigotOfflinePlayer(org.bukkit.OfflinePlayer op) {
		this.op = op;
	}
	
	@Override
	public boolean isOnGround() {
		return true;
	}

	@Override
	public boolean isOp() {
		return op.isOp();
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public double getEyeHeight() {
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public int getEntityId() {
		return 0;
	}

	@Override
	public Object getDefaultEntity() {
		return op;
	}

	@Override
	public void sendMessage(String msg) {
		
	}

	@Override
	public String getName() {
		return op.getName();
	}

	@Override
	public boolean isOnline() {
		return op.isOnline();
	}

	@Override
	public UUID getUniqueId() {
		return op.getUniqueId();
	}

	@Override
	public boolean hasPlayedBefore() {
		return op.hasPlayedBefore();
	}

}
