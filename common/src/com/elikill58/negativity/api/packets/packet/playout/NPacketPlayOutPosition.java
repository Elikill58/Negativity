package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutPosition implements NPacketPlayOut, LocatedPacket {

	public double x, y, z;
	public float yaw, pitch;
	public EnumPlayerTeleportFlags flag;

	public NPacketPlayOutPosition() {

	}

	@Override
	public void read(PacketSerializer serializer) {
		this.x = serializer.readDouble();
		this.y = serializer.readDouble();
		this.z = serializer.readDouble();
		this.yaw = serializer.readFloat();
		this.pitch = serializer.readFloat();
		this.flag = EnumPlayerTeleportFlags.values()[serializer.readUnsignedByte()];
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
		return PacketType.Server.POSITION;
	}

	public enum EnumPlayerTeleportFlags {
		X, Y, Z, Y_ROT, X_ROT;

	}
}
