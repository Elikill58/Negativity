package com.elikill58.negativity.api.packets;

public abstract class PacketHandler {

	/**
	 * Method called when the server receive a packet
	 * 
	 * @param packet the received packet
	 */
	public abstract void onReceive(AbstractPacket packet);
	
	/**
	 * Method called when the server send a packet
	 * 
	 * @param packet the sent packet
	 */
	public abstract void onSend(AbstractPacket packet);
}
