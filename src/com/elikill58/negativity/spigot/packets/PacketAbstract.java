package com.elikill58.negativity.spigot.packets;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.ChannelInjector.ChannelWrapper;

@SuppressWarnings("rawtypes")
public abstract class PacketAbstract {

	private Player player;
	private ChannelWrapper<?> channelWrapper;

	private Object packet;

	public PacketAbstract(Object packet, Player player) {
		this.player = player;
		this.packet = packet;
	}
	
	public PacketAbstract(Object packet, ChannelWrapper channelWrapper) {
		this.channelWrapper = channelWrapper;
		this.packet = packet;
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean hasPlayer() {
		return this.player != null;
	}
	
	public ChannelWrapper<?> getChannel() {
		return this.channelWrapper;
	}

	public boolean hasChannel() {
		return this.channelWrapper != null;
	}

	public String getPlayername() {
		return hasPlayer() ? null : player.getName();
	}

	public Object getPacket() {
		return this.packet;
	}

	public String getPacketName() {
		return this.packet.getClass().getSimpleName();
	}
	
	public static interface IPacketListener {
		Object onPacketReceive(Object sender, Object packet);
	}
}