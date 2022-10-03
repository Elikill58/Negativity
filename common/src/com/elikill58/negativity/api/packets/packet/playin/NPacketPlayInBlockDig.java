package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInBlockDig implements NPacketPlayIn, LocatedPacket {

	public int x = 0, y = 0, z = 0;
	public BlockFace face;
	public DigAction action;
	
	public NPacketPlayInBlockDig() {
		
	}

	public NPacketPlayInBlockDig(int x, int y, int z, DigAction action, BlockFace face) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.action = action;
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
