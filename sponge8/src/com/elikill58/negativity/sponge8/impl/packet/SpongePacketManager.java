package com.elikill58.negativity.sponge8.impl.packet;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketManager;
import com.elikill58.negativity.sponge8.SpongeNegativity;

public abstract class SpongePacketManager extends PacketManager {
	
	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Sponge.server().scheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.execute(() -> {
					PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
					EventManager.callEvent(event);
				}).build()
		);
	}
	
	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Sponge.server().scheduler().submit(
			Task.builder()
				.plugin(SpongeNegativity.container())
				.execute(() -> {
					PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
					EventManager.callEvent(event);
				}).build()
		);
	}
}
