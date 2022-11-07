package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInEntityAction implements NPacketPlayIn {

	public int entityId, sequence;
	public EnumPlayerAction action;
	
	public NPacketPlayInEntityAction() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
		this.action = serializer.getEnum(EnumPlayerAction.class);
		this.sequence = serializer.readVarInt();
	}
	
	public static enum EnumPlayerAction {

		START_SNEAKING,
		STOP_SNEAKING,
		STOP_SLEEPING,
		START_SPRINTING,
		STOP_SPRINTING,
		START_RIDING_JUMP,
		STOP_RIDING_JUMP,
		OPEN_INVENTORY,
		START_FALL_FLYING,
		LEAVE_BED;
		
		/**
		 * Get the action according to the given key
		 * 
		 * @param key the name of the action
		 * @return the enum or null
		 */
		public static EnumPlayerAction getAction(String key) {
			key = key.toUpperCase();
			for(EnumPlayerAction action : values())
				if(action.name().equalsIgnoreCase(key))
					return action;
			return null;
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.ENTITY_ACTION;
	}
}
