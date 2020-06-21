package com.elikill58.negativity.sponge.packets.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

import com.elikill58.negativity.sponge.packets.AbstractPacket;
import com.elikill58.negativity.sponge.SpongeNegativity;

public abstract class PacketEvent extends AbstractEvent implements TargetPlayerEvent {
	
	private final Player p;
	private final AbstractPacket packet;
	private final PacketSourceType source;
	
	public PacketEvent(PacketSourceType source, AbstractPacket packet, Player p) {
		this.source = source;
		this.packet = packet;
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	public PacketSourceType getPacketSourceType() {
		return source;
	}
    
    public enum PacketSourceType {
    	PACKETGATE;
    }
	
	@Override
	public Cause getCause() {
		return Cause.builder().append(SpongeNegativity.INSTANCE).append(p).build(EventContext.empty());
	}

	@Override
	public Player getTargetEntity() {
		return p;
	}
}
