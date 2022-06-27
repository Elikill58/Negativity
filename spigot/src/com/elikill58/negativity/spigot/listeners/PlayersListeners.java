package com.elikill58.negativity.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.api.events.player.PlayerTeleportEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent.ToggleAction;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

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
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> NegativityPlayer.removeFromCache(p.getUniqueId()), 2);
	}
	
	@EventHandler
	public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC") || e.isCancelled())
			return;
		Block below = p.getLocation().clone().subtract(0, 1, 0).getBlock();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		if(np.isFreeze && !below.getType().equals(Material.AIR)) {
			e.setCancelled(true);
			return;
		}
		PlayerMoveEvent event = new PlayerMoveEvent(np.getPlayer(), SpigotLocation.toCommon(e.getFrom()), SpigotLocation.toCommon(e.getTo()));
		EventManager.callEvent(event);
		if(event.hasToSet()) {
			e.setTo(SpigotLocation.fromCommon(event.getTo()));
		}
		if(event.isCancelled()) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		PlayerChatEvent event = new PlayerChatEvent(SpigotEntityManager.getPlayer(e.getPlayer()), e.getMessage(), e.getFormat());
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
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
	public void onItemConsume(PlayerItemConsumeEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotItemStack(e.getItem())));
	}
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC")) {
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
		EventManager.callEvent(new PlayerTeleportEvent(SpigotEntityManager.getPlayer(e.getPlayer()), SpigotLocation.toCommon(e.getFrom()), SpigotLocation.toCommon(e.getTo())));
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
	
	@EventHandler
	public void onToggleFly(PlayerToggleFlightEvent e) {
		PlayerToggleActionEvent toggleEvent = new PlayerToggleActionEvent(SpigotEntityManager.getPlayer(e.getPlayer()), ToggleAction.FLY, e.isCancelled());
		EventManager.callEvent(toggleEvent);
		e.setCancelled(toggleEvent.isCancelled()); // can do right now because the event take the cancellation of the bukkit event
	}
	
	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent e) {
		PlayerToggleActionEvent toggleEvent = new PlayerToggleActionEvent(SpigotEntityManager.getPlayer(e.getPlayer()), ToggleAction.SNEAK, e.isCancelled());
		EventManager.callEvent(toggleEvent);
		e.setCancelled(toggleEvent.isCancelled()); // can do right now because the event take the cancellation of the bukkit event
	}
	
	@EventHandler
	public void onToggleSprint(PlayerToggleSprintEvent e) {
		PlayerToggleActionEvent toggleEvent = new PlayerToggleActionEvent(SpigotEntityManager.getPlayer(e.getPlayer()), ToggleAction.SPRINT, e.isCancelled());
		EventManager.callEvent(toggleEvent);
		e.setCancelled(toggleEvent.isCancelled()); // can do right now because the event take the cancellation of the bukkit event
	}
}
