package com.elikill58.negativity.velocity.impl.entity;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.negativity.api.entity.AbstractProxyPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Version;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

import net.kyori.text.TextComponent;

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
	public String getIP() {
		return pp.getRemoteAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return pp.isActive();
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersionByProtocolID(pp.getProtocolVersion().getProtocol());
	}

	@Override
	public boolean isOp() {
		return pp.hasPermission("*");
	}

	@Override
	public void sendMessage(String msg) {
		pp.sendMessage(TextComponent.of(msg));
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
	public boolean hasPermission(String perm) {
		return pp.hasPermission(perm);
	}
	
	@Override
	public void kick(String reason) {
		pp.disconnect(TextComponent.of(reason));
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		pp.sendPluginMessage(new LegacyChannelIdentifier(channelId), writeMessage);
	}

	@Override
	public int getPing() {
		return (int) pp.getPing();
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return pp.getRemoteAddress();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
