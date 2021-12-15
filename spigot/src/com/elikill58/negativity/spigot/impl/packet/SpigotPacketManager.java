package com.elikill58.negativity.spigot.impl.packet;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketManager;

public abstract class SpigotPacketManager extends PacketManager {

	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		if(packet.getPacketType() == null || packet.getNmsPacket() == null)
			return;
		PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
		EventManager.callEvent(event);
		handlers.forEach((handler) -> handler.onReceive(packet));
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		if(packet.getPacketType() == null || packet.getNmsPacket() == null)
			return;
		PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
		EventManager.callEvent(event);
		handlers.forEach((handler) -> handler.onSend(packet));
	}
}
