package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutRelEntityMoveLook extends NPacketPlayOutEntity {

	@Override
	public void read(PacketSerializer serializer, Version version) {
		super.read(serializer, version);
		if(version.isNewerOrEquals(Version.V1_13)) {
			this.deltaX = ((double) serializer.readShort()) / 4096;
			this.deltaY = ((double) serializer.readShort()) / 4096;
			this.deltaZ = ((double) serializer.readShort()) / 4096;
		} else {
			this.deltaX = ((double) serializer.readByte()) / 32;
			this.deltaY = ((double) serializer.readByte()) / 32;
			this.deltaZ = ((double) serializer.readByte()) / 32;
		}
		this.yaw = serializer.readByte();
		this.pitch = serializer.readByte();
		this.isGround = serializer.readUnsignedByte() != 0;
	}

	@Override
	public PacketType getPacketType() {
		return Server.REL_ENTITY_MOVE_LOOK;
	}
}
