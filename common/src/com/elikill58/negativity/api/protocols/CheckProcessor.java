package com.elikill58.negativity.api.protocols;

import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.packets.PrePacketSendEvent;

public interface CheckProcessor {

	/**
	 * Begin the check processor
	 */
	default void begin() {};
	
	/**
	 * Handle a packet when it's received from client. Just check for which type is it
	 * 
	 * @param e the event of the packet
	 */
	default void handlePacketReceived(PacketReceiveEvent e) {}
	
	/**
	 * Handle a packet when it's send from the server to client. Just check for which type is it
	 * 
	 * @param e the event of the packet
	 */
	default void handlePacketSent(PacketSendEvent e) {};
	
	/**
	 * Handle a packet when it's send from the server to client. Just check for which type is it
	 * 
	 * @param e the event of the packet
	 */
	default void handlePacketSent(PrePacketSendEvent e) {};
	
	/**
	 * End the check processor
	 */
	default void stop() {};
	
}
