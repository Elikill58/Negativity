package com.elikill58.negativity.spigot.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;

public class PlayersEvents implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		LoginEvent event = new LoginEvent(e.getUniqueId(), e.getName(), Result.valueOf(e.getLoginResult().name()), e.getAddress(), e.getKickMessage());
		EventManager.callEvent(event);
		e.setKickMessage(event.getKickMessage());
		e.setLoginResult(AsyncPlayerPreLoginEvent.Result.valueOf(event.getLoginResult().name()));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, e.getJoinMessage());
		EventManager.callEvent(event);
		e.setJoinMessage(event.getJoinMessage());
		
		if(!ProxyCompanionManager.searchedCompanion) {
			ProxyCompanionManager.searchedCompanion = true;
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> SpigotNegativity.sendProxyPing(p), 20);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, e.getQuitMessage());
		EventManager.callEvent(event);
		e.setQuitMessage(event.getQuitMessage());
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> NegativityPlayer.removeFromCache(p.getUniqueId()), 2);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		if(np.isFreeze && !p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR))
			e.setCancelled(true);
	}

	@EventHandler
	public void slimeManager(PlayerMoveEvent e){
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		if(p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("SLIME")) {
			np.isUsingSlimeBlock = true;
		} else if(np.isUsingSlimeBlock && (p.isOnGround() && !p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("AIR")))
			np.isUsingSlimeBlock = false;
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		NegativityPlayer.getCached(e.getPlayer().getUniqueId()).TIME_INVINCIBILITY = System.currentTimeMillis() + 2000;
	}
}
