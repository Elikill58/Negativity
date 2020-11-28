package com.elikill58.negativity.api.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.PacketType;

public abstract class AbstractPacket {

	protected final Player player;
	protected final Object packet;
	protected PacketContent content;
	protected PacketType type;
	protected boolean cancel = false;
	
	public AbstractPacket(PacketType type, Object packet, Player player) {
		this.player = player;
		this.packet = packet;
		this.type = type;
		this.content = new PacketContent(this);
	}

	/**
	 * Get the player concerned by the packet.
	 * This player can have sent or received this packet.
	 * 
	 * @return the concerned player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Check if the given player exist
	 * 
	 * @return true if there is a valid player
	 */
	public boolean hasPlayer() {
		return player != null;
	}
	
	/**
	 * Get the name of the player
	 * 
	 * @return the player name
	 */
	public String getPlayername() {
		return getPlayer().getName();
	}

	/**
	 * Get the NMS packet
	 * 
	 * @return the sent/received packet
	 */
	public Object getPacket() {
		return packet;
	}

	/**
	 * Get the name of the packet
	 * 
	 * @return the packet name
	 */
	public String getPacketName() {
		return packet.getClass().getSimpleName();
	}
	
	/**
	 * Get the packet type.
	 * Can be a Client or a Server.
	 * Currently, Login and Status packet are NOT supported (because it's not used yet)
	 * 
	 * @return the type of the packet
	 */
	public PacketType getPacketType() {
		return type;
	}
	
	/**
	 * Know if the packet is cancelled.
	 * 
	 * @return true if it's cancel
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Set if the packet is cancelled
	 */
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * Get the packet content without using NMS
	 * And so, compatible with all version.
	 * 
	 * @return the packet content
	 */
	public PacketContent getContent() {
		return content;
	}
}
