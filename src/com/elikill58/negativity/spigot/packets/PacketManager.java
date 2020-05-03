package com.elikill58.negativity.spigot.packets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.packets.event.PacketEvent.PacketSourceType;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.spigot.packets.event.PacketSendEvent;

public interface PacketManager {

	void addPlayer(Player p);

	void removePlayer(Player p);
	
	void clear();

	final List<PacketHandler> handlers = new ArrayList<>();

	default boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	default boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
	
	default void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
			Bukkit.getPluginManager().callEvent(event);
			handlers.forEach((handler) -> handler.onReceive(packet));
		});
	}

	default void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		if(!SpigotNegativity.getInstance().isEnabled()) // cannot go on main thread is plugin doesn't enabled
			return;
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
			Bukkit.getPluginManager().callEvent(event);
			handlers.forEach((handler) -> handler.onSend(packet));
		});
	}
}
