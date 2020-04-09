package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;

public class PlayersEventsManager {

	@Listener
	public void slimeManager(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(p.getLocation().sub(0, 1, 0).getBlock().getType().getId().contains("SLIME")) {
			np.isUsingSlimeBlock = true;
		} else if(np.isUsingSlimeBlock && p.isOnGround())
			np.isUsingSlimeBlock = false;
	}
}
