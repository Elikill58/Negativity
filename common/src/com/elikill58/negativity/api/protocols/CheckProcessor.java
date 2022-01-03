package com.elikill58.negativity.api.protocols;

import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;

public interface CheckProcessor {

	/**
	 * Begin the check processor
	 */
	default void begin() {};
	
	/**
	 * Handle a packet. Just check for which type is it
	 * 
	 * @param e the event of the packet
	 */
	void handlePacketReceived(PacketReceiveEvent e);
	
	/**
	 * End the check processor
	 */
	default void stop() {};
	
}
