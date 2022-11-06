package com.elikill58.negativity.sponge.impl.packet;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.Packet;
import com.elikill58.negativity.api.packets.PacketManager;

public abstract class SpongePacketManager extends PacketManager {
	
	public void notifyHandlersReceive(PacketSourceType source, Packet packet) {
		PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
		EventManager.callEvent(event);
	}
	
	public void notifyHandlersSent(PacketSourceType source, Packet packet) {
		PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
		EventManager.callEvent(event);
	}
}
