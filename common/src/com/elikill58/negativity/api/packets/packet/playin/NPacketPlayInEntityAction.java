package com.elikill58.negativity.api.packets.packet.playin;

import java.util.Arrays;
import java.util.List;

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
		START_SNEAKING("a", "PRESS_SHIFT_KEY"),
		STOP_SNEAKING("b", "RELEASE_SHIFT_KEY"),
		STOP_SLEEPING("c"),
		START_SPRINTING("d"),
		STOP_SPRINTING("e"),
		START_RIDING_JUMP("f", "RIDING_JUMP"),
		STOP_RIDING_JUMP("g"),
		OPEN_INVENTORY("h"),
		START_FALL_FLYING("i"),
		LEAVE_BED("j");
		
		private final List<String> alias;
		
		private EnumPlayerAction(String... alias) {
			this.alias = Arrays.asList(alias);
		}
		
		public List<String> getAlias() {
			return alias;
		}
		
		/**
		 * Get the action according to the given key
		 * 
		 * @param key the name of the action
		 * @return the enum or null
		 */
		public static EnumPlayerAction getAction(String key) {
			key = key.toUpperCase();
			for(EnumPlayerAction action : values())
				if(action.name().equalsIgnoreCase(key) || action.getAlias().contains(key))
					return action;
			return null;
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.ENTITY_ACTION;
	}
}
