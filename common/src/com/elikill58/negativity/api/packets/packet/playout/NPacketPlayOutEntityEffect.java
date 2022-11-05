package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Version;

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
	public boolean showParticles;
	
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
	public void read(PacketSerializer serializer, Version version) {
	    this.entityId = serializer.readVarInt();
	    this.type = PotionEffectType.fromId(serializer.readByte());
	    this.amplifier = serializer.readByte();
	    this.duration = serializer.readVarInt();
	    this.flags = serializer.readByte();
	    this.showParticles = flags == 1;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_EFFECT;
	}
}
