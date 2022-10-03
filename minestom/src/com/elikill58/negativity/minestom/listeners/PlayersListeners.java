package com.elikill58.negativity.minestom.listeners;

import java.util.Locale;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent.ToggleAction;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.entity.MinestomPlayer;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;
import com.elikill58.negativity.minestom.impl.location.MinestomLocation;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerStartFlyingEvent;
import net.minestom.server.event.player.PlayerStartFlyingWithElytraEvent;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.event.player.PlayerStartSprintingEvent;
import net.minestom.server.event.player.PlayerStopFlyingEvent;
import net.minestom.server.event.player.PlayerStopSneakingEvent;
import net.minestom.server.event.player.PlayerStopSprintingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;

public class PlayersListeners {

	public PlayersListeners(EventNode<Event> e) {
		e.addListener(PlayerDisconnectEvent.class, this::onQuit);
		e.addListener(net.minestom.server.event.player.PlayerMoveEvent.class, this::onMove);
		e.addListener(net.minestom.server.event.player.PlayerChatEvent.class, this::onChat);
		e.addListener(PlayerDeathEvent.class, this::onDeath);
		e.addListener(PlayerBlockInteractEvent.class, this::onInteract);
		e.addListener(PlayerEntityInteractEvent.class, this::onInteract);
		e.addListener(PlayerUseItemEvent.class, this::onItemConsume);
		e.addListener(PlayerStartFlyingEvent.class, this::onStartFly);
		e.addListener(PlayerStopFlyingEvent.class, this::onStopFly);
		e.addListener(PlayerStartSneakingEvent.class, this::onStartSneak);
		e.addListener(PlayerStopSneakingEvent.class, this::onStopSneak);
		e.addListener(PlayerStartSprintingEvent.class, this::onStartSprint);
		e.addListener(PlayerStopSprintingEvent.class, this::onStopSprint);
		e.addListener(PlayerSpawnEvent.class, this::onLogin);
		e.addListener(AsyncPlayerPreLoginEvent.class, this::onPreLogin);
		e.addListener(PlayerCommandEvent.class, this::onCommand);
	}

	public void onQuit(PlayerDisconnectEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUuid(), () -> new MinestomPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, null);
		EventManager.callEvent(event);
	}
	
	public void onMove(net.minestom.server.event.player.PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(e.isCancelled())
			return;
		Block below = e.getInstance().getBlock(p.getPosition().sub(0, 1, 0));
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUuid(), () -> new MinestomPlayer(p));
		if(np.isFreeze && !below.equals(Block.AIR)) {
			e.setCancelled(true);
			return;
		}
		PlayerMoveEvent event = new PlayerMoveEvent(np.getPlayer(), MinestomLocation.toCommon(e.getInstance(), e.getPlayer().getPosition()), MinestomLocation.toCommon(e.getInstance(), e.getNewPosition()));
		EventManager.callEvent(event);
		if(event.hasToSet()) {
			e.setNewPosition(MinestomLocation.fromCommon(event.getTo()));
		}
		if(event.isCancelled()) {
			e.setCancelled(true);
			return;
		}
	}
	
	public void onChat(net.minestom.server.event.player.PlayerChatEvent e) {
		PlayerChatEvent event = new PlayerChatEvent(MinestomEntityManager.getPlayer(e.getPlayer()), e.getMessage(), null);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	// TODO fix player damaged by entity event
	/*public void onDamageByEntity(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player)
			EventManager.callEvent(new PlayerDamagedByEntityEvent(MinestomEntityManager.getPlayer((Player) e.getEntity()), MinestomEntityManager.getEntity(e.getDamager())));
	}*/
	
	public void onDeath(PlayerDeathEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.player.PlayerDeathEvent(MinestomEntityManager.getPlayer(e.getEntity())));
	}
	
	public void onInteract(PlayerBlockInteractEvent e) {
		PlayerInteractEvent event = new PlayerInteractEvent(MinestomEntityManager.getPlayer(e.getPlayer()), Action.LEFT_CLICK_BLOCK);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	public void onInteract(PlayerEntityInteractEvent e) {
		EventManager.callEvent(new PlayerInteractEvent(MinestomEntityManager.getPlayer(e.getPlayer()), Action.LEFT_CLICK_AIR));
	}
	
	public void onItemConsume(PlayerUseItemEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent(MinestomEntityManager.getPlayer(e.getPlayer()), new MinestomItemStack(e.getItemStack())));
	}
	
	// TODO fix regein health
	/*public void onRegainHealth(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player) {
			PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(MinestomEntityManager.getPlayer((Player) e.getEntity()));
			EventManager.callEvent(event);
			if(event.isCancelled())
				e.setCancelled(event.isCancelled());
		}
	}*/
	
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		LoginEvent event = new LoginEvent(e.getPlayerUuid(), e.getUsername(), Result.ALLOWED, null, null);
		EventManager.callEvent(event);
		// TODO fix address null & few changes
	}
	
	// TODO find player teleport event
	/*public void onTeleport(EntityTeleportPacket e) {
		EventManager.callEvent(new PlayerTeleportEvent(MinestomEntityManager.getPlayer(e.getPlayer()), SpigotLocation.toCommon(e.getFrom()), SpigotLocation.toCommon(e.getTo())));
	}*/
	
	public void onLogin(PlayerSpawnEvent e) {
		if(!e.isFirstSpawn())
			return;
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUuid(), () -> new MinestomPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, null);
		EventManager.callEvent(event);
	}

	public void onGlide(PlayerStartFlyingWithElytraEvent e) {
		NegativityPlayer.getCached(e.getPlayer().getUuid()).timeInvincibility = System.currentTimeMillis() + 800;
	}
	
	public void onStartFly(PlayerStartFlyingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.FLY, false));
	}
	
	public void onStopFly(PlayerStopFlyingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.FLY, false));
	}
	
	public void onStartSneak(PlayerStartSneakingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SNEAK, false));
	}
	
	public void onStopSneak(PlayerStopSneakingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SNEAK, false));
	}
	
	public void onStartSprint(PlayerStartSprintingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SPRINT, false));
	}
	
	public void onStopSprint(PlayerStopSprintingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SPRINT, false));
	}
	
	public void onCommand(PlayerCommandEvent e) {
		String message = e.getCommand();
		String cmd = message.split(" ")[0];
		String[] arg = message.replace(cmd + " ", "").split(" ");
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		PlayerCommandPreProcessEvent event = new PlayerCommandPreProcessEvent(MinestomEntityManager.getPlayer(e.getPlayer()), cmd, arg, prefix, false);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(true);
	}
}
