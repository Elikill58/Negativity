package com.elikill58.negativity.api.packets.packet.playout;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutPosition implements NPacketPlayOut, LocatedPacket {

	/**
	 * Since 1.9
	 */
	public int teleportId;
	/**
	 * Warn: those values can be relative (and not absolute) according to {@link #flags}
	 */
	public double x, y, z;
	public float yaw, pitch;
	public List<EnumPlayerTeleportFlags> flags;
	/**
	 * Since 1.17 until 1.19.3
	 */
	public boolean shouldDismount;

	public NPacketPlayOutPosition() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.x = serializer.readDouble();
		this.y = serializer.readDouble();
		this.z = serializer.readDouble();
		this.yaw = serializer.readFloat();
		this.pitch = serializer.readFloat();
		this.flags = EnumPlayerTeleportFlags.get(serializer.readUnsignedByte());
		if (version.isNewerOrEquals(Version.V1_9)) {
			this.teleportId = serializer.readVarInt();
			if (version.isNewerOrEquals(Version.V1_17) && !version.isNewerOrEquals(Version.V1_19_4))
				this.shouldDismount = serializer.readBoolean();
		}
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

		private int charge() {
			return 1 << ordinal();
		}

		private boolean isValid(int param1Int) {
			return ((param1Int & charge()) == charge());
		}

		/**
		 * Get flags values according to given packet value
		 * 
		 * @param val the value from packet
		 * @return all flags
		 */
		public static List<EnumPlayerTeleportFlags> get(int val) {
			List<EnumPlayerTeleportFlags> flags = new ArrayList<>();
			for (EnumPlayerTeleportFlags enumPlayerTeleportFlags : values())
				if (enumPlayerTeleportFlags.isValid(val))
					flags.add(enumPlayerTeleportFlags);
			return flags;
		}
	}
}
