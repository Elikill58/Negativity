package com.elikill58.negativity.minestom.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.packet.FabricPacketManager;
import com.elikill58.negativity.minestom.nms.MinestomVersionAdapter;
import com.elikill58.negativity.universal.Adapter;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

public class NegativityPacketManager extends FabricPacketManager {
	
	public NegativityPacketManager(EventNode<Event> e) {
		e.addListener(PlayerPacketEvent.class, this::onPacket);
		e.addListener(PlayerPacketOutEvent.class, this::onPacket);
	}
	
	public void onPacket(PlayerPacketEvent e) {
		Player p = MinestomEntityManager.getPlayer(e.getPlayer());
		NPacket commonPacket = MinestomVersionAdapter.getVersionAdapter().getPacket(p, PacketDirection.CLIENT_TO_SERVER, e.getPacket());
		if(commonPacket == null) {
			return;
		}
		e.setCancelled(onPacketReceive(commonPacket, p, e.getPacket()).isCancelled());
	}
	
	public void onPacket(PlayerPacketOutEvent e) {
		Player p = MinestomEntityManager.getPlayer(e.getPlayer());
		NPacket commonPacket = MinestomVersionAdapter.getVersionAdapter().getPacket(p, PacketDirection.SERVER_TO_CLIENT, e.getPacket());
		if(commonPacket == null) {
			return;
		}
		e.setCancelled(onPacketSent(commonPacket, p, e.getPacket()).isCancelled());
	}
	
	public AbstractPacket onPacketSent(NPacket packet, Player sender, Object nmsPacket) {
		MinestomPacket customPacket = new MinestomPacket(packet, nmsPacket, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket packet, Player sender, Object nmsPacket) {
		if(packet.getPacketType().isUnset())
			Adapter.getAdapter().debug("Received unset: " + packet.getClass().getSimpleName() + " > " + MinestomVersionAdapter.getVersionAdapter().getNameOfPacket(nmsPacket));
		MinestomPacket customPacket = new MinestomPacket(packet, nmsPacket, sender);
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}
}
