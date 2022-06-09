package com.elikill58.negativity.api.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.packet.NPacket;

public abstract class AbstractPacket {

	protected final Player player;
	protected Object nmsPacket;
	protected PacketContent content;
	protected final NPacket nPacket;
	protected boolean cancel = false;
	
	public AbstractPacket(Object nmsPacket, NPacket nPacket, Player player) {
		this.player = player;
		this.nmsPacket = nmsPacket;
		this.nPacket = nPacket;
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
	public Object getNmsPacket() {
		return nmsPacket;
	}
	
	/**
	 * Change the given NMS packet
	 * 
	 * @param nmsPacket the next packet
	 */
	public void setNmsPacket(Object nmsPacket) {
		this.nmsPacket = nmsPacket;
	}
	
	/**
	 * Get the Negativity Packet which correspond to the NMS one
	 * 
	 * @return the sent/received packet
	 */
	public NPacket getPacket() {
		return nPacket;
	}

	/**
	 * Get the name of the packet
	 * 
	 * @return the packet name
	 */
	public String getPacketName() {
		return nmsPacket.getClass().getSimpleName();
	}
	
	/**
	 * Get the packet type.
	 * Can be a Client or a Server.
	 * Currently, Login and Status packet are NOT supported (because it's not used yet)
	 * 
	 * @return the type of the packet
	 */
	public PacketType getPacketType() {
		return nPacket.getPacketType();
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
	 * 
	 * @param cancel if the packet should be cancelled or not
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
