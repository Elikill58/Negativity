package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.potion.PotionEffectType;

public class NPacketPlayOutEntityEffect implements NPacketPlayOut {

	public int entityId;
	public PotionEffectType type;
	public byte amplifier;
	public int duration;
	/**
	 * Warn: the meaning of this variable change over the time.<br>
	 * We not support it.
	 */
	public byte flags; // don't support this variable

	public NPacketPlayOutEntityEffect() {}
	
	public NPacketPlayOutEntityEffect(int entityId, int typeId, byte amplifier, int duration, byte flags) {
		this.entityId = entityId;
		this.type = PotionEffectType.fromId(typeId);
		this.amplifier = amplifier;
		this.duration = duration;
		this.flags = flags;
	}
	
	public NPacketPlayOutEntityEffect(int entityId, PotionEffectType type, byte amplifier, int duration, byte flags) {
		this.entityId = entityId;
		this.type = type;
		this.amplifier = amplifier;
		this.duration = duration;
		this.flags = flags;
	}
	
	public boolean isMaxDuration() {
		return duration >= 32767;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_EFFECT;
	}
}
