package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUseItem implements NPacketPlayIn {

	public Hand hand;
	
	public NPacketPlayInUseItem() {
		
	}
	
	public NPacketPlayInUseItem(Hand hand) {
		this.hand = hand;
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.USE_ITEM;
	}
}
