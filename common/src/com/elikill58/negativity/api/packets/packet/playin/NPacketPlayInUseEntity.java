package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUseEntity implements NPacketPlayIn {

	public int entityId;
	/**
	 * WARN: this value seems to be not present in 1.17+
	 */
	public Vector vector;
	public EnumEntityUseAction action;
	public Hand hand;

	public NPacketPlayInUseEntity() {

	}

	@Override
	public void read(PacketSerializer serializer) {
		this.entityId = serializer.readVarInt();
		this.action = serializer.getEnum(EnumEntityUseAction.class);
		if (this.action == EnumEntityUseAction.INTERACT_AT)
			this.vector = new Vector(serializer.readFloat(), serializer.readFloat(), serializer.readFloat());
		if (this.action == EnumEntityUseAction.INTERACT || this.action == EnumEntityUseAction.INTERACT_AT)
			this.hand = serializer.getEnum(Hand.class);
	}

	public static enum EnumEntityUseAction {
		INTERACT, ATTACK, INTERACT_AT;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.USE_ENTITY;
	}
}
