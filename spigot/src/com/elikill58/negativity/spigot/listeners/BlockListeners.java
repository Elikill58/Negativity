package com.elikill58.negativity.spigot.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class BlockListeners implements Listener {

	@EventHandler
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
		Player p = SpigotEntityManager.getPlayer(e.getPlayer());
		Block b = e.getBlock();
		BlockPlaceEvent event = new BlockPlaceEvent(p, new SpigotBlock(b), new SpigotBlock(e.getBlockAgainst()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
}
