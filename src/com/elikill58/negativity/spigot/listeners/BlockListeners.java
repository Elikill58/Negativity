package com.elikill58.negativity.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class BlockListeners implements Listener {

	@EventHandler
	public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
		BlockBreakEvent event = new BlockBreakEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotBlock(e.getBlock()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}

	@EventHandler
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
		BlockPlaceEvent event = new BlockPlaceEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotBlock(e.getBlock()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
}
