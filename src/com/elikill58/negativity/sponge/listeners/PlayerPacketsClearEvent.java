package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;

public class PlayerPacketsClearEvent extends AbstractEvent implements TargetPlayerEvent {
	
	private Player p;
	private NegativityPlayer np;
	
	public PlayerPacketsClearEvent(Player p, NegativityPlayer np) {
		this.p = p;
		this.np = np;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
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
