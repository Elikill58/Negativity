package com.elikill58.negativity.spigot.impl.packet;

import org.bukkit.Bukkit;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketManager;
import com.elikill58.negativity.spigot.SpigotNegativity;

public abstract class SpigotPacketManager extends PacketManager {
	
	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onReceive(packet));
		});
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onSend(packet));
		});
	}
}
