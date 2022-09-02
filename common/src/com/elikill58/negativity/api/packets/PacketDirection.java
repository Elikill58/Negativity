package com.elikill58.negativity.api.packets;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.elikill58.negativity.api.packets.packet.NPacketUnset;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeUnset;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;

public enum PacketDirection {

	CLIENT_TO_SERVER("PacketPlayIn", PacketType.Client.values(), PacketType.Client.UNSET, NPacketPlayInUnset::new),
	SERVER_TO_CLIENT("PacketPlayOut", PacketType.Server.values(), PacketType.Server.UNSET, NPacketPlayOutUnset::new),
	STATUS("PacketStatus", PacketType.Status.values(), PacketType.Status.UNSET, NPacketStatusUnset::new),
	LOGIN("PacketLogin", PacketType.Login.values(), PacketType.Login.UNSET, NPacketLoginUnset::new),
	HANDSHAKE("PacketHandshaking", PacketType.Handshake.values(), PacketType.Handshake.UNSET, NPacketHandshakeUnset::new);

	private final List<PacketType> types;
	private final String prefix;
	private final PacketType unset;
	private final Function<String, NPacketUnset> unsetCreator;

	private PacketDirection(String prefix, PacketType[] types, PacketType unset, Function<String, NPacketUnset> unsetCreator) {
		this.prefix = prefix;
		this.types = Arrays.asList(types);
		this.unset = unset;
		this.unsetCreator = unsetCreator;
	}

	public String getPrefix() {
		return prefix;
	}

	public List<PacketType> getTypes() {
		return types;
	}
	
	public PacketType getUnset() {
		return unset;
	}
	
	public NPacketUnset createUnsetPacket(String packetName) {
		return unsetCreator.apply(packetName);
	}
}
