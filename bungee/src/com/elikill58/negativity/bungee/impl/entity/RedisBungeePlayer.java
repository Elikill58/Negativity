package com.elikill58.negativity.bungee.impl.entity;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.negativity.api.entity.AbstractProxyPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

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
		InetAddress addr = RedisBungee.getApi().getPlayerIp(uuid);
		return addr == null ? null : addr.getHostAddress();
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
		return getPlayerVersion().getFirstProtocolNumber();
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
		return ProxyServer.getInstance().getPlayer(uuid);
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
		ServerInfo si = RedisBungee.getApi().getServerFor(uuid);
		return si == null ? null : si.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
