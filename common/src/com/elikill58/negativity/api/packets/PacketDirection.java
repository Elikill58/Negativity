package com.elikill58.negativity.api.packets;

import java.util.Arrays;
import java.util.List;

public enum PacketDirection {

	CLIENT_TO_SERVER("PacketPlayIn", PacketType.Client.values(), PacketType.Client.UNSET),
	SERVER_TO_CLIENT("PacketPlayOut", PacketType.Server.values(), PacketType.Server.UNSET),
	STATUS("PacketStatus", PacketType.Status.values(), PacketType.Status.UNSET),
	LOGIN("PacketLogin", PacketType.Login.values(), PacketType.Login.UNSET),
	HANDSHAKE("PacketHandshaking", PacketType.Handshake.values(), PacketType.Handshake.UNSET);

	private final List<PacketType> types;
	private final String prefix;
	private final PacketType unset;

	private PacketDirection(String prefix, PacketType[] types, PacketType unset) {
		this.prefix = prefix;
		this.types = Arrays.asList(types);
		this.unset = unset;
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
}
