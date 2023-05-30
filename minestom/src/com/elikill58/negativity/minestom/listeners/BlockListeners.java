package com.elikill58.negativity.minestom.listeners;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.minestom.impl.block.MinestomBlock;
import com.elikill58.negativity.minestom.impl.entity.MinestomPlayer;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class BlockListeners {

	public BlockListeners(EventNode<Event> e) {
		e.addListener(PlayerBlockPlaceEvent.class, this::onPlaceEvent);
	}
	
	public void onPlaceEvent(PlayerBlockPlaceEvent e) {
		BlockPlaceEvent event = new BlockPlaceEvent(new MinestomPlayer(e.getPlayer()), new MinestomBlock(e.getInstance().getBlock(e.getBlockPosition()), e.getInstance(), e.getBlockPosition()), new MinestomBlock(e.getBlock(), e.getInstance(), e.getBlockPosition()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
}
