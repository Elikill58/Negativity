package com.elikill58.negativity.api.packets.packet.playout;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutExplosion implements NPacketPlayOut, LocatedPacket {

	public double x, y, z, strength;
	public Vector vec;
	public List<BlockPosition> positions;

	public NPacketPlayOutExplosion() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		if (version.isNewerOrEquals(Version.V1_19_3)) {
			this.x = serializer.readDouble();
			this.y = serializer.readDouble();
			this.z = serializer.readDouble();
		} else {
			this.x = serializer.readFloat();
			this.y = serializer.readFloat();
			this.z = serializer.readFloat();
		}
		this.strength = serializer.readFloat();
		int i = serializer.readVarInt();
		this.positions = new ArrayList<>(i);
		for (int b = 0; b < i; b++) {
			int posX = (int) (serializer.readByte() + x);
			int posY = (int) (serializer.readByte() + y);
			int posZ = (int) (serializer.readByte() + z);
			this.positions.add(new BlockPosition(posX, posY, posZ));
		}
		this.vec = serializer.readVector();
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.EXPLOSION;
	}
}
