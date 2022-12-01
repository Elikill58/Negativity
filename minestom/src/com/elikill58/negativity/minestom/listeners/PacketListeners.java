package com.elikill58.negativity.minestom.listeners;

import java.nio.ByteBuffer;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.nms.MinestomPlayPackets;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.server.ServerPacket;

public class PacketListeners {

	private NamedVersion serverVersion;

	public PacketListeners(EventNode<Event> e) {
		this.serverVersion = Version.getVersion().getOrCreateNamedVersion();
		e.addListener(PlayerPacketEvent.class, this::onPacket);
		e.addListener(PlayerPacketOutEvent.class, this::onPacket);
	}
	
	private NamedVersion getNamedVersion(Player p) {
		return p.getPlayerVersion().equals(Version.getVersion()) ? serverVersion : PlayerVersionManager.getPlayerVersion(p).getOrCreateNamedVersion(); // get server if it's same
	}
	
	private NPacket getPacketFromWriter(Player p, NetworkBuffer.Writer writer, int packetId, PacketDirection dir) {
		NetworkBuffer buffer = new NetworkBuffer();
		writer.write(buffer);
		ByteBuffer nioBuffer = (ByteBuffer) ReflectionUtils.getField(buffer, "nioBuffer");
		NPacket packet = getNamedVersion(p).getPacket(dir, packetId);
		if(packet == null) {
			return null;
		}
		try {
			ByteBuf buf = Unpooled.wrappedBuffer(nioBuffer);
			packet.read(new PacketSerializer(buf), p.getPlayerVersion());
		} catch (IndexOutOfBoundsException exc) {
			Adapter.getAdapter().getLogger().warn("Failed to read packet with ID " + packetId + " for player " + p.getName() + " (" + dir.name() + " - decode)");
			return null;
		}
		return packet;
	}
	
	public void onPacket(PlayerPacketEvent e) {
		ClientPacket cp = e.getPacket();
		Integer packetId = MinestomPlayPackets.getPacketId(cp.getClass());
		if(packetId == null) {
			Adapter.getAdapter().getLogger().warn("Unknow packet ID for " + cp.getClass());
			return;
		}
		Player p = MinestomEntityManager.getPlayer(e.getEntity());
		NPacket packet = getPacketFromWriter(p, cp, packetId, PacketDirection.CLIENT_TO_SERVER);
		if(packet == null) {
			return;
		}
		PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(true);
	}
	
	public void onPacket(PlayerPacketOutEvent e) {
		ServerPacket sp = e.getPacket();
		Player p = MinestomEntityManager.getPlayer(e.getEntity());
		NPacket packet = getPacketFromWriter(p, sp, sp.getId(), PacketDirection.SERVER_TO_CLIENT);
		if(packet == null) {
			return;
		}
		EventManager.callEvent(new PacketSendEvent(packet, p));
	}
}
