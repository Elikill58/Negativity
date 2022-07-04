package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInBlockDig implements NPacketPlayIn, LocatedPacket {

	public int x, y, z;
	public DigFace face;
	public DigAction action;
	
	public NPacketPlayInBlockDig() {
		
	}

	public NPacketPlayInBlockDig(int x, int y, int z, DigAction action, DigFace face) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.action = action;
	}
	
	@Override
	public boolean hasLocation() {
		return (action == DigAction.START_DIGGING || action == DigAction.CANCEL_DIGGING || action == DigAction.FINISHED_DIGGING) && LocatedPacket.super.hasLocation();
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

	public static enum DigFace {

		BOTTOM(0),
		TOP(1),
		NORTH(2),
		SOUTH(3),
		WEST(4),
		EAST(5);
		
		private final int id;
		
		private DigFace(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		public static DigFace getById(int id) {
			for(DigFace da : values())
				if(da.getId() == id)
					return da;
			return null;
		}
	}
}
