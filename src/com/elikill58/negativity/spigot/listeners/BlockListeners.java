package com.elikill58.negativity.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class BlockListeners implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.block.BlockBreakEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotBlock(e.getBlock())));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.block.BlockPlaceEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotBlock(e.getBlock())));
	}
}
