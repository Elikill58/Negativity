package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class BlinkProtocol extends Cheat {

	public BlinkProtocol() {
		super("BLINK", true, ItemTypes.COAL_BLOCK, false, true);
	}
	
	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death e, @First Player p){
		SpongeNegativityPlayer.getNegativityPlayer(p).bypassBlink = true;
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p){
		SpongeNegativityPlayer.getNegativityPlayer(p).bypassBlink = false;
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
