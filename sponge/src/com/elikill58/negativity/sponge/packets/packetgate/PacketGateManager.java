package com.elikill58.negativity.sponge.packets.packetgate;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.Packet;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.impl.packet.SpongePacketManager;
import com.elikill58.negativity.sponge.nms.SpongeVersionAdapter;
import com.elikill58.negativity.universal.Adapter;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.plugin.PluginPacketGate;

public class PacketGateManager extends SpongePacketManager {

	public PacketGateManager() {
		PluginPacketGate.packetGate.registerListener(new PacketGateListener(this), ListenerPriority.DEFAULT);
	}

	public Packet onPacketSent(NPacket packet, ServerPlayer sender, Object nmsPacket, PacketEvent event) {
		if(packet == null || packet.getPacketType() == null)
			return null;
		/*if(packet.getPacketType().isUnset())
			Adapter.getAdapter().debug("Unset PacketType sent for " + nmsPacket.getClass().getCanonicalName());*/
		Packet customPacket = new Packet(packet, nmsPacket, SpongeEntityManager.getPlayer(sender));
		notifyHandlersSent(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public Packet onPacketReceive(NPacket packet, ServerPlayer sender, Object nmsPacket, PacketEvent event) {
		if(packet == null || packet.getPacketType() == null) 
			return null;
		if(packet.getPacketType().isUnset())
			Adapter.getAdapter().debug("Unset PacketType receive for " + ((NPacketPlayInUnset) packet).packetName + " : " + nmsPacket.getClass().getName());
		Packet customPacket = new Packet(packet, nmsPacket, SpongeEntityManager.getPlayer(sender));
		notifyHandlersReceive(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public class PacketGateListener extends PacketListenerAdapter {

		private final PacketGateManager packetManager;

		public PacketGateListener(PacketGateManager packetManager) {
			this.packetManager = packetManager;
		}

		@Override
		public void onPacketRead(PacketEvent e, PacketConnection connection) {
			UUID uuid = connection.playerUniqueId();
			if(uuid == null)
				return;
			Optional<ServerPlayer> optionalPlayer = Sponge.server().player(uuid);
			if (!optionalPlayer.isPresent())
				return;
			ServerPlayer p = optionalPlayer.get();
			SpongeVersionAdapter ada = SpongeVersionAdapter.getVersionAdapter();
			net.minecraft.network.protocol.Packet<?> nmsPacket = e.packet();
			Packet packet = packetManager.onPacketReceive(ada.getPacket(p, PacketDirection.CLIENT_TO_SERVER, nmsPacket), p, nmsPacket, e);
			if(packet != null)
				e.setCancelled(packet.isCancelled());
		}
		
		@Override
		public void onPacketWrite(PacketEvent e, PacketConnection connection) {
			UUID uuid = connection.playerUniqueId();
			if(uuid == null)
				return;
			Optional<ServerPlayer> optionalPlayer = Sponge.server().player(uuid);
			if (!optionalPlayer.isPresent())
				return;
			ServerPlayer p = optionalPlayer.get();
			SpongeVersionAdapter ada = SpongeVersionAdapter.getVersionAdapter();
			net.minecraft.network.protocol.Packet<?> nmsPacket = e.packet();
			Packet packet = packetManager.onPacketSent(ada.getPacket(p, PacketDirection.SERVER_TO_CLIENT, nmsPacket), p, nmsPacket, e);
			if(packet != null)
				e.setCancelled(packet.isCancelled());
		}
	}
}
