package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerChangeWorldEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.api.events.player.PlayerTeleportEvent;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.impl.location.SpigotWorld;
import com.elikill58.negativity.universal.Adapter;

public class PlayersListeners implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, e.getQuitMessage());
		EventManager.callEvent(event);
		e.setQuitMessage(event.getQuitMessage());
		Adapter.getAdapter().getScheduler().runDelayed(() -> NegativityPlayer.removeFromCache(p.getUniqueId()), 2);
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity().hasMetadata("NPC"))
			return;
		if(e.getEntity() instanceof Player)
			EventManager.callEvent(new PlayerDamagedByEntityEvent(SpigotEntityManager.getPlayer((Player) e.getEntity()), SpigotEntityManager.getEntity(e.getDamager())));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(e.getEntity().hasMetadata("NPC"))
			return;
		EventManager.callEvent(new com.elikill58.negativity.api.events.player.PlayerDeathEvent(SpigotEntityManager.getPlayer(e.getEntity())));
	}
	
	@EventHandler
	public void onInteract(org.bukkit.event.player.PlayerInteractEvent e) {
		PlayerInteractEvent event = new PlayerInteractEvent(SpigotEntityManager.getPlayer(e.getPlayer()), Action.valueOf(e.getAction().name()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		org.bukkit.World old = e.getPlayer().getWorld();
		EventManager.callEvent(new PlayerChangeWorldEvent(SpigotEntityManager.getPlayer(e.getPlayer()), World.getWorld(old.getName(), (a) -> new SpigotWorld(old))));
	}
	
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotItemStack(e.getItem())));
	}
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC") && (e.getRegainReason().equals(RegainReason.EATING) || e.getRegainReason().equals(RegainReason.SATIATED))) {
			PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(SpigotEntityManager.getPlayer((Player) e.getEntity()));
			EventManager.callEvent(event);
			if(event.isCancelled())
				e.setCancelled(event.isCancelled());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		LoginEvent event = new LoginEvent(e.getUniqueId(), e.getName(), Result.valueOf(e.getLoginResult().name()), e.getAddress(), e.getKickMessage());
		EventManager.callEvent(event);
		e.setKickMessage(event.getKickMessage());
		e.setLoginResult(AsyncPlayerPreLoginEvent.Result.valueOf(event.getLoginResult().name()));
	}
	
	@EventHandler
	public void onTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
		if(e.getPlayer().hasMetadata("NPC"))
			return;
		com.elikill58.negativity.api.entity.Player p = SpigotEntityManager.getPlayer(e.getPlayer());
		EventManager.callEvent(new PlayerTeleportEvent(p, SpigotLocation.toCommon(e.getFrom(), p), SpigotLocation.toCommon(e.getTo(), p)));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, e.getJoinMessage());
		EventManager.callEvent(event);
		e.setJoinMessage(event.getJoinMessage());
	}
}
