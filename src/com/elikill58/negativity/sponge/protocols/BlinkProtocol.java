package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.sponge.NeedListener;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;

public class BlinkProtocol implements NeedListener {
	
	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death e, @First Player p){
		SpongeNegativityPlayer.getNegativityPlayer(p).bypassBlink = true;
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p){
		SpongeNegativityPlayer.getNegativityPlayer(p).bypassBlink = false;
	}
}
