package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.sponge.impl.block.SpongeBlock;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;

public class BlockListeners {

	@Listener
	public void onBlockChange(ChangeBlockEvent.All e, @First ServerPlayer p) {
		/*e.transactions(Operations.BREAK.get())
			.forEach(transaction -> {
				BlockBreakEvent event = new BlockBreakEvent(SpongeEntityManager.getPlayer(p), new SpongeBlock(transaction.original()));
				EventManager.callEvent(event);
				if (event.isCancelled()) {
					transaction.invalidate();
				}
			});*/
		e.transactions(Operations.PLACE.get())
			.forEach(transaction -> {
				BlockPlaceEvent event = new BlockPlaceEvent(SpongeEntityManager.getPlayer(p), new SpongeBlock(transaction.original()));
				EventManager.callEvent(event);
				if (event.isCancelled()) {
					transaction.invalidate();
				}
			});
	}
}
