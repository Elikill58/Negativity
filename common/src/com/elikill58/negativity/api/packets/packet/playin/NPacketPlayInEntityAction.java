package com.elikill58.negativity.api.packets.packet.playin;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInEntityAction implements NPacketPlayIn {

	public int entityId, c;
	public EnumPlayerAction action;
	
	public NPacketPlayInEntityAction() {
		
	}
	
	/**
	 * Create a new entity action packet
	 * 
	 * @param entityId the Id of the concerned ID
	 * @param action the action made by the entity
	 * @param c unknow value yet
	 */
	public NPacketPlayInEntityAction(int entityId, EnumPlayerAction action, int c) {
		this.entityId = entityId;
		this.action = action;
		this.c = c;
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
