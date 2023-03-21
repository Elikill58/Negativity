package com.elikill58.negativity.common.server;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.impl.server.CompensatedWorld;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutCustomPayload;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutMultiBlockChange;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.logger.Debug;

public class NegativityPacketOutListener implements Listeners {

	@EventListener
	public void onPacketPreSend(PacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NPacket packet = (NPacket) e.getPacket();
		PacketType type = packet.getPacketType();
		if(type.equals(PacketType.Server.BLOCK_CHANGE)) {
			NPacketPlayOutBlockChange change = (NPacketPlayOutBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			if(change.type != null) // type founded
				w.addTimingBlock(p.getPing(), change.type, change.pos.getX(), change.pos.getY(), change.pos.getZ());
		} else if(type.equals(PacketType.Server.MULTI_BLOCK_CHANGE)) {
			NPacketPlayOutMultiBlockChange change = (NPacketPlayOutMultiBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			change.blockStates.forEach((pos, m) -> w.addTimingBlock(p.getPing(), m, pos.getX(), pos.getY(), pos.getZ()));
		} else if(type.equals(PacketType.Server.CUSTOM_PAYLOAD)) {
			NPacketPlayOutCustomPayload a = (NPacketPlayOutCustomPayload) packet;
			Adapter.getAdapter().debug(Debug.GENERAL, "Channel: " + a.channel);
		}
	}
}
