package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUseEntity implements NPacketPlayIn {

	public int entityId;
	public Vector vector; // WARN: this value seems to be not present in 1.17+
	public EnumEntityUseAction action;

	public NPacketPlayInUseEntity() {

	}

	public NPacketPlayInUseEntity(int entityId, Vector vector, EnumEntityUseAction action) {
		this.entityId = entityId;
		this.vector = vector;
		this.action = action;
	}

	public static enum EnumEntityUseAction {
		INTERACT, ATTACK, INTERACT_AT;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.USE_ENTITY;
	}
}
