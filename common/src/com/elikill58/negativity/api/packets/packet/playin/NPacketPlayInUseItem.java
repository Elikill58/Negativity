package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

/**
 * Interact packet. Badly named on spigot, please ignore the spigot naming for BlockPlace/UseItem.
 * <p>
 * Only exist since 1.9
 */
public class NPacketPlayInUseItem implements NPacketPlayIn {

	public Hand hand;
	/**
	 * This field is only available since 1.19
	 */
	public int sequence;
	
	public NPacketPlayInUseItem() {
		
	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.hand = serializer.getEnum(Hand.class);
		if(version.isNewerOrEquals(Version.V1_19))
			this.sequence = serializer.readVarInt();
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.USE_ITEM;
	}
}
