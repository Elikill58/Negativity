package com.elikill58.orebfuscator.packets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.event.PacketReceiveEvent;
import com.elikill58.orebfuscator.packets.event.PacketSendEvent;
import com.elikill58.orebfuscator.packets.event.PacketEvent.PacketSourceType;

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
		PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
		Bukkit.getPluginManager().callEvent(event);
		for (PacketHandler handler : handlers)
			handler.onReceive(packet);
	}

	public default void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
		Bukkit.getPluginManager().callEvent(event);
		for (PacketHandler handler : handlers)
			handler.onSend(packet);
	}
}
