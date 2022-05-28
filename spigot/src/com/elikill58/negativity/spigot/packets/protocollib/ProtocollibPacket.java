package com.elikill58.negativity.spigot.packets.protocollib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class ProtocollibPacket extends AbstractPacket {

	private PacketEvent event;

	public ProtocollibPacket(NPacket nPacket, Object nmsPacket, Player p, PacketEvent event) {
		super(nmsPacket, nPacket, SpigotEntityManager.getPlayer(p));
		this.event = event;
	}

	public PacketEvent getProtocollibEvent() {
		return event;
	}
}
