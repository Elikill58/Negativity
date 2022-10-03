package com.elikill58.negativity.minestom.listeners;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.minestom.impl.block.MinestomBlock;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.entity.MinestomPlayer;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class BlockListeners {

	public BlockListeners(EventNode<Event> e) {
		e.addListener(PlayerBlockBreakEvent.class, this::onBreakEvent);
		e.addListener(PlayerBlockPlaceEvent.class, this::onPlaceEvent);
	}
	
	public void onBreakEvent(PlayerBlockBreakEvent e) {
		BlockBreakEvent event = new BlockBreakEvent(MinestomEntityManager.getPlayer(e.getPlayer()), new MinestomBlock(e.getBlock(), e.getInstance(), e.getBlockPosition()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	public void onPlaceEvent(PlayerBlockPlaceEvent e) {
		BlockPlaceEvent event = new BlockPlaceEvent(new MinestomPlayer(e.getPlayer()), new MinestomBlock(e.getInstance().getBlock(e.getBlockPosition()), e.getInstance(), e.getBlockPosition()), new MinestomBlock(e.getBlock(), e.getInstance(), e.getBlockPosition()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
}
