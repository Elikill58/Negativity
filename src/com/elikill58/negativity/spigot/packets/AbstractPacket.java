package com.elikill58.negativity.spigot.packets;

import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.PacketType;

public abstract class AbstractPacket {

	protected Player player;
	protected Object packet;
	protected PacketType type;
	protected boolean cancel = false;
	
	public AbstractPacket(PacketType type, Object packet, Player player) {
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
		return getPlayer().getName();
	}

	public Object getPacket() {
		return packet;
	}

	public String getPacketName() {
		return packet.getClass().getSimpleName();
	}
	
	public PacketType getPacketType() {
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
