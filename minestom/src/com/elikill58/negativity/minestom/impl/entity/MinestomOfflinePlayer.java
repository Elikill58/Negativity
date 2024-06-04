package com.elikill58.negativity.minestom.impl.entity;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.OfflinePlayer;

import net.minestom.server.MinecraftServer;
import net.minestom.server.network.ConnectionManager;

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
		return uuid != null && MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid) != null;
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
		ConnectionManager cm = MinecraftServer.getConnectionManager();
		return name == null ? (uuid == null ? "??" : uuid.toString()) : (cm.getOnlinePlayerByUuid(uuid) == null ? uuid.toString() : cm.getOnlinePlayerByUuid(uuid).getUsername());
	}

	@Override
	public @Nullable Object getDefault() {
		return null;
	}
}
