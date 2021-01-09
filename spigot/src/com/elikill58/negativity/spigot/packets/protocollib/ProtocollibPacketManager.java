package com.elikill58.negativity.spigot.packets.protocollib;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.universal.PacketType;

public class ProtocollibPacketManager extends SpigotPacketManager {

	private final ProtocolManager protocolManager;
	
	public ProtocollibPacketManager(Plugin pl) {
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(pl, ListenerPriority.LOWEST, PacketRegistry.getClientPacketTypes()) {
			
			@Override
			public void onPacketSending(PacketEvent e) {
		        if(e.isPlayerTemporary())
		        	return;
				AbstractPacket packet = onPacketSent(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
						e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}

			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player p = e.getPlayer();
		        if (p == null)
		            return;
		        if(e.isPlayerTemporary())
		        	return;
		        AbstractPacket packet = onPacketReceive(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
		        		e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}
		});
		//List<com.comphenix.protocol.PacketType> packetToDontUse= Arrays.asList(Play.Server.MAP, Play.Server.ENTITY_METADATA);
		//List<com.comphenix.protocol.PacketType> packets = Play.Server.getInstance().values().stream().filter(com.comphenix.protocol.PacketType::isSupported).filter(packetToDontUse::contains).collect(Collectors.toList());
		//protocolManager.addPacketListener(new PacketAdapter(pl, ListenerPriority.LOWEST, Play.Server.MAP, Play.Server.ENTITY_METADATA) {
		List<com.comphenix.protocol.PacketType> packets = Arrays.asList(Play.Server.ENTITY_VELOCITY, Play.Server.ENTITY_EFFECT, Play.Server.REMOVE_ENTITY_EFFECT);
		protocolManager.addPacketListener(new PacketAdapter(pl, ListenerPriority.LOWEST, packets) {
			@Override
			public void onPacketSending(PacketEvent e) {
		        if(e.isPlayerTemporary())
		        	return;
				AbstractPacket packet = onPacketSent(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
						e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}

			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player p = e.getPlayer();
		        if (p == null)
		            return;
		        if(e.isPlayerTemporary())
		        	return;
		        AbstractPacket packet = onPacketReceive(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
		        		e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}
		});
	}
	
	@Override
	public void addPlayer(com.elikill58.negativity.api.entity.Player p) {}
	
	@Override
	public void removePlayer(com.elikill58.negativity.api.entity.Player p) {}

	@Override
	public void clear() {}

	public AbstractPacket onPacketSent(PacketType type, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(type, packet, sender, event);
		notifyHandlersSent(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(PacketType type, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(type, packet, sender, event);
		notifyHandlersReceive(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}
}
