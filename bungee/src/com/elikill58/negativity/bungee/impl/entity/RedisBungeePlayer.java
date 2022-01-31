package com.elikill58.negativity.bungee.impl.entity;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.negativity.api.entity.AbstractProxyPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;

public class RedisBungeePlayer extends AbstractProxyPlayer {

	private final UUID uuid;
	
	public RedisBungeePlayer(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}
	
	@Override
	public String getIP() {
		return RedisBungee.getApi().getPlayerIp(uuid).getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return RedisBungee.getApi().isPlayerOnline(uuid);
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersion();
	}
	
	@Override
	public int getProtocolVersion() {
		return 0;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void sendMessage(String msg) {
		
	}

	@Override
	public String getName() {
		return RedisBungee.getApi().getNameFromUuid(uuid);
	}

	@Override
	public Object getDefault() {
		return null;
	}

	@Override
	public boolean hasPermission(String perm) {
		return false;
	}
	
	@Override
	public void kick(String reason) {
		RedisBungee.getApi().sendProxyCommand("kick " + getName() + " " + reason);
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		RedisBungee.getApi().sendChannelMessage(channelId, new String(writeMessage));
	}

	@Override
	public int getPing() {
		return 0;
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return null;
	}
	
	@Override
	public void sendToServer(String serverName) {
		RedisBungee.getApi().sendProxyCommand("send " + getName() + " " + serverName);
	}
	
	@Override
	public String getServerName() {
		return RedisBungee.getApi().getServerFor(uuid).getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
