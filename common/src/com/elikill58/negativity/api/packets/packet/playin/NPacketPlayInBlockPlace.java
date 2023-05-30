package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

/**
 * Block place packet. Sometimes named "UseItemOn".<br>
 * Some field are not present, such as:<br>
 * - sequence (int) : no use, no description<br>
 * 
 */
public class NPacketPlayInBlockPlace implements NPacketPlayIn, LocatedPacket {

	public Hand hand = Hand.MAIN;
	public BlockFace face;
	public BlockPosition pos;
	public boolean insideBlock;
	public float cursorX, cursorY, cursorZ;

	public NPacketPlayInBlockPlace() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		if (version.isNewerOrEquals(Version.V1_14))
			this.hand = serializer.getEnum(Hand.class);
		this.pos = serializer.readBlockPosition(version);
		this.face = BlockFace.getById(serializer.readUnsignedByte());
		if (version.isNewerOrEquals(Version.V1_9) && !version.isNewerOrEquals(Version.V1_14)) // between 1.9 to 1.13
			this.hand = serializer.getEnum(Hand.class);
		if (version.equals(Version.V1_8)) {
			serializer.readItemStack(version); // skip item index
			this.cursorX = serializer.readUnsignedByte() / 16.0F;
			this.cursorY = serializer.readUnsignedByte() / 16.0F;
			this.cursorZ = serializer.readUnsignedByte() / 16.0F;
		} else {
			this.cursorX = serializer.readFloat();
			this.cursorY = serializer.readFloat();
			this.cursorZ = serializer.readFloat();
		}
		if (version.isNewerOrEquals(Version.V1_13))
			this.insideBlock = serializer.readBoolean();
	}

	@Override
	public double getX() {
		return pos.getX();
	}

	@Override
	public double getY() {
		return pos.getY();
	}

	@Override
	public double getZ() {
		return pos.getZ();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.BLOCK_PLACE;
	}
}
