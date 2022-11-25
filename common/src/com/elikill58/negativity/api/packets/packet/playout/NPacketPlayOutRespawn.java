package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutRespawn implements NPacketPlayOut {

	public String worldName;
	public Difficulty difficulty;
	public GameMode gamemode = GameMode.CUSTOM;

	@Override
	public void read(PacketSerializer serializer, Version version) {
		if(version.isNewerOrEquals(Version.V1_19)) { // TODO check since which version it is
			serializer.readString();
			this.worldName = serializer.readString();
			serializer.readLong(); // hashed seed
			this.gamemode = serializer.getEnum(GameMode.class);
		} else {
			serializer.readInt(); // ignore
			this.difficulty = Difficulty.values()[serializer.readUnsignedByte()];
			this.gamemode = GameMode.values()[serializer.readUnsignedByte()];
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.RESPAWN;
	}
}
