package com.elikill58.negativity.spigot.packets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketHandler;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public abstract class PacketManager {

	public abstract void addPlayer(Player p);
	public abstract void removePlayer(Player p);
	public abstract void clear();

	private final List<PacketHandler> handlers = new ArrayList<>();
	public boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	public boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
	
	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketReceiveEvent event = new PacketReceiveEvent(source, packet, SpigotEntityManager.getPlayer(packet.getPlayer()));
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onReceive(packet));
		});
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketSendEvent event = new PacketSendEvent(source, packet, SpigotEntityManager.getPlayer(packet.getPlayer()));
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onSend(packet));
		});
	}
}
