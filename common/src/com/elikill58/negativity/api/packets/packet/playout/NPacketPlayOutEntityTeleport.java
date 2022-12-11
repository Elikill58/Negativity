package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutEntityTeleport implements NPacketPlayOut, LocatedPacket {

	public int entityId;
	public double x, y, z;
	public float yaw;
	public float pitch;
	public boolean onGround;
	
	public NPacketPlayOutEntityTeleport() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
	    this.entityId = serializer.readVarInt();
	    if(version.isNewerOrEquals(Version.V1_9)) {
		    this.x = serializer.readDouble();
		    this.y = serializer.readDouble();
		    this.z = serializer.readDouble();
	    } else {
		    this.x = serializer.readInt() / 32;
		    this.y = serializer.readInt() / 32;
		    this.z = serializer.readInt() / 32;
	    }
	    this.yaw = serializer.readByte();
	    this.pitch = serializer.readByte();
	    this.onGround = serializer.readBoolean();
	}
	
	@Override
	public Location getLocation(World w) {
		return new Location(w, x, y, z, yaw, pitch);
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
		return PacketType.Server.ENTITY_TELEPORT;
	}
}
