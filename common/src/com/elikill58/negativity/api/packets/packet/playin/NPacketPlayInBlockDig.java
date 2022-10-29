package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInBlockDig implements NPacketPlayIn, LocatedPacket {

	public DigAction action;
	public BlockPosition pos;
	public BlockFace face;
	
	public NPacketPlayInBlockDig() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		this.action = DigAction.getById(serializer.readVarInt());
		if(Version.getVersion().isNewerOrEquals(Version.V1_19))
			this.pos = serializer.readBlockPositionNew();
		else
			this.pos = serializer.readBlockPosition();
		this.face = BlockFace.getById(serializer.readUnsignedByte());
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
		return PacketType.Client.BLOCK_DIG;
	}
	
	public static enum DigAction {

		START_DIGGING(0),
		CANCEL_DIGGING(1),
		FINISHED_DIGGING(2),
		
		/**
		 * Drop the entire of the item stack
		 */
		DROP_ITEM_STACK(3),
		
		/**
		 * Drop one item
		 */
		DROP_ITEM(4),
		
		/**
		 * The finished action can correspond to:<br>
		 * - Shoot arrow<br>
		 * - Finish eating<br>
		 * - Use bucket<br>
		 * - ...
		 */
		FINISH_ACTION(5),
		
		/**
		 * Action when item is swipped between two item
		 */
		SWAP_ITEM(6);
		
		private final int id;
		
		private DigAction(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		public static DigAction getById(int id) {
			for(DigAction da : values())
				if(da.getId() == id)
					return da;
			return null;
		}
	}
}
