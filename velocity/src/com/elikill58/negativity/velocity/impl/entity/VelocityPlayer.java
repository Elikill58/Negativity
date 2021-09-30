package com.elikill58.negativity.velocity.impl.entity;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.negativity.api.entity.AbstractProxyPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

import net.kyori.adventure.text.Component;

public class VelocityPlayer extends AbstractProxyPlayer {
	
	private final com.velocitypowered.api.proxy.Player pp;
	
	public VelocityPlayer(com.velocitypowered.api.proxy.Player pp) {
		this.pp = pp;
	}
	
	@Override
	public UUID getUniqueId() {
		return pp.getUniqueId();
	}
	
	@Override
	public boolean isOnline() {
		return pp.isActive();
	}
	
	@Override
	public void sendMessage(String msg) {
		pp.sendMessage(Component.text(msg));
	}
	
	@Override
	public String getIP() {
		return pp.getRemoteAddress().getAddress().getHostAddress();
	}
	
	@Override
	public boolean hasPermission(String perm) {
		return pp.hasPermission(perm);
	}
	
	@Override
	public int getPing() {
		return (int) pp.getPing();
	}
	
	@Override
	public void kick(String reason) {
		pp.disconnect(Component.text(reason));
	}
	
	@Override
	public Version getPlayerVersion() {
		return Version.getVersionByProtocolID(pp.getProtocolVersion().getProtocol());
	}
	
	@Override
	public int getProtocolVersion() {
		return pp.getProtocolVersion().getProtocol();
	}
	
	@Override
	public void setProtocolVersion(int protocolVersion) {
		// don't need it on velocity
	}
	
	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		pp.sendPluginMessage(new LegacyChannelIdentifier(channelId), writeMessage);
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return pp.getRemoteAddress();
	}
	
	@Override
	public boolean isOp() {
		return pp.hasPermission("*");
	}
	
	@Override
	public String getName() {
		return pp.getUsername();
	}
	
	@Override
	public Object getDefault() {
		return pp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
