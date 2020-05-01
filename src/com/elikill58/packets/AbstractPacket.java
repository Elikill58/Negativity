package com.elikill58.orebfuscator.packets;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.PacketType.AbstractPacketType;

public abstract class AbstractPacket {

	protected Player player;
	protected Object packet;
	protected AbstractPacketType type;
	protected boolean cancel = false;
	
	public AbstractPacket(AbstractPacketType type, Object packet, Player player) {
		this.player = player;
		this.packet = packet;
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean hasPlayer() {
		return player != null;
	}
	
	public String getPlayername() {
		return hasPlayer() ? null : getPlayer().getName();
	}

	public Object getPacket() {
		return packet;
	}

	public String getPacketName() {
		return packet.getClass().getSimpleName();
	}
	
	public AbstractPacketType getPacketType() {
		return type;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public PacketContent getContent() {
		return new PacketContent(this);
	}
}
