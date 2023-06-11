package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInSettings implements NPacketPlayIn {

	public String locale;
	public int viewDistance;
	public Hand mainHand = Hand.MAIN;
	public int displayedSkinParts;
	public ChatMode chatMode;

	@Override
	public void read(PacketSerializer serializer, Version v) {
		this.locale = serializer.readString(v.isNewerOrEquals(Version.V1_16) ? 256 : (v.isNewerOrEquals(Version.V1_12) ? 16 : 7));
		this.viewDistance = serializer.readByte();
		this.chatMode = serializer.getEnum(ChatMode.class);
		serializer.readBoolean(); // read some unused part
		this.displayedSkinParts = serializer.readUnsignedByte();
		if (v.isNewerOrEquals(Version.V1_9))
			this.mainHand = serializer.readVarInt() == 0 ? Hand.OFF : Hand.MAIN;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.SETTINGS;
	}

	public static enum ChatMode {
		ENABLED, COMMAND_ONLY, HIDDEN;
	}
}
