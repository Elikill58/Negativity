package com.elikill58.negativity.spigot.packets;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.ChannelInjector.ChannelWrapper;

public class ReceivedPacket extends PacketAbstract {
	
	public ReceivedPacket(Object packet, Player player) {
		super(packet, player);
	}

	public ReceivedPacket(Object packet, ChannelWrapper<?> channelWrapper) {
		super(packet, channelWrapper);
	}
}
