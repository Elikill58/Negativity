package com.elikill58.negativity.spigot.packets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.packets.event.PacketEvent.PacketSourceType;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.spigot.packets.event.PacketSendEvent;

public interface IPacketManager {

	public void addPlayer(Player p);

	public void removePlayer(Player p);
	
	public void clear();

	final List<PacketHandler> handlers = new ArrayList<>();

	public default boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	public default boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
	
	public default void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
				Bukkit.getPluginManager().callEvent(event);
				for (PacketHandler handler : handlers)
					handler.onReceive(packet);
			}
		});
	}

	public default void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
				Bukkit.getPluginManager().callEvent(event);
				for (PacketHandler handler : handlers)
					handler.onSend(packet);
			}
		});
	}
}
