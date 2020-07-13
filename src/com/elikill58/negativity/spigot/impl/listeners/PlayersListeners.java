package com.elikill58.negativity.spigot.impl.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.events.EventManager;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.impl.events.PlayerCheatEvent;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

public class PlayersListeners implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		NegativityPlayer.getNegativityPlayer(new SpigotPlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		NegativityPlayer.removeFromCache(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
		EventManager.callEvent(new PlayerMoveEvent(NegativityPlayer.getCached(e.getPlayer().getUniqueId()).getPlayer(),
				new SpigotLocation(e.getFrom()), new SpigotLocation(e.getTo())));
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
			EventManager.callEvent(new PlayerCheatEvent(NegativityPlayer.getCached(e.getPlayer().getUniqueId()).getPlayer(), null, 100));
		});
	}
}
