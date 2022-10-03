package com.elikill58.negativity.spigot.packets.protocollib;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

public class ProtocollibPacketManager extends SpigotPacketManager {

	private final ProtocolManager protocolManager;

	public ProtocollibPacketManager(Plugin pl) {
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(
				new NegativityPacketAdapter(pl, ListenerPriority.LOWEST, PacketRegistry.getClientPacketTypes()));
		protocolManager.addPacketListener(new NegativityPacketAdapter(pl, ListenerPriority.LOWEST,
				Arrays.asList(Server.ENTITY_VELOCITY, Server.ENTITY_EFFECT, Server.BLOCK_BREAK_ANIMATION,
						Server.KEEP_ALIVE, Server.EXPLOSION, Server.POSITION, Server.ENTITY_TELEPORT, Server.PING)));
	}

	public AbstractPacket onPacketSent(NPacket commonPacket, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(commonPacket, packet, sender, event);
		if (commonPacket == null) {
			return customPacket;
		}
		notifyHandlersSent(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket commonPacket, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(commonPacket, packet, sender, event);
		if (commonPacket == null) {
			return customPacket;
		}
		notifyHandlersReceive(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}

	private class NegativityPacketAdapter extends PacketAdapter {

		private NegativityPacketAdapter(Plugin plugin, ListenerPriority priority,
				Iterable<? extends PacketType> types) {
			super(plugin, priority, types);
		}

		@Override
		public void onPacketSending(PacketEvent e) {
			if (e.isPlayerTemporary()) {
				return;
			}
			Player player = e.getPlayer();
			if (player == null || player.hasMetadata("NPC")) {
				return;
			}
			Object nmsPacket = e.getPacket().getHandle();
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(player,
					PacketDirection.SERVER_TO_CLIENT, nmsPacket);
			if (commonPacket == null)
				return;
			AbstractPacket packet = ProtocollibPacketManager.this.onPacketSent(commonPacket, player, nmsPacket, e);
			if (!e.isCancelled()) {
				e.setCancelled(packet.isCancelled());
			}
		}

		@Override
		public void onPacketReceiving(PacketEvent e) {
			if (e.isPlayerTemporary()) {
				return;
			}
			Player player = e.getPlayer();
			if (player == null || player.hasMetadata("NPC")) {
				return;
			}
			Object nmsPacket = e.getPacket().getHandle();
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(player,
					PacketDirection.CLIENT_TO_SERVER, nmsPacket);
			if (commonPacket == null)
				return;
			AbstractPacket packet = ProtocollibPacketManager.this.onPacketReceive(commonPacket, player, nmsPacket, e);
			if (!e.isCancelled()) {
				e.setCancelled(packet.isCancelled());
			}
		}
	}
}
