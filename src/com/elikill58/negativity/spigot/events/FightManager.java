package com.elikill58.negativity.spigot.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.Potion;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

@SuppressWarnings("deprecation")
public class FightManager implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
			return;
		SpigotNegativityPlayer.getNegativityPlayer((Player) e.getDamager()).fight();
		SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).fight();
	}

	@EventHandler
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerFillBucket(PlayerBucketFillEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 10);
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		switch(p.getItemInHand().getType()) {
		case DIAMOND_SWORD:
		case IRON_SWORD:
		case STONE_SWORD:
		case GOLDEN_SWORD:
		case WOODEN_SWORD:
		case DIAMOND_AXE:
		case IRON_AXE:
		case STONE_AXE:
		case GOLDEN_AXE:
		case WOODEN_AXE:
		case BOW:
		case POTION:
		case GOLDEN_APPLE:
			manageFightBetweenTwoPlayers(p, 20);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!e.getEntity().getType().equals(EntityType.SPLASH_POTION) || !(e.getEntity() instanceof Potion))
			return;
		Location loc = e.getEntity().getLocation();
		switch(((Potion) e.getEntity()).getType()) {
		case NIGHT_VISION:
		case INVISIBILITY:
		case WATER:
		case WATER_BREATHING:
			return;
		case FIRE_RESISTANCE:
		case INSTANT_HEAL:
		case REGEN:
		case SPEED:
		case STRENGTH:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 18 && loc.distance(p.getLocation()) > 4)
						SpigotNegativityPlayer.getNegativityPlayer(p).fight();
			break;
		case INSTANT_DAMAGE:
		case POISON:
		case WEAKNESS:
		case SLOWNESS:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 9)
						SpigotNegativityPlayer.getNegativityPlayer(p).fight();
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		SpigotNegativityPlayer.getNegativityPlayer(e.getEntity()).unfight();
	}
	
	private void manageFightBetweenTwoPlayers(Player p, int maxDistance) {
		if(!p.getWorld().getPVP())
			return;
		for(Player pl : Utils.getOnlinePlayers())
			if(pl.getLocation().getWorld().equals(p.getLocation().getWorld()))
				if(pl.getLocation().distance(p.getLocation()) < 8) {
					SpigotNegativityPlayer.getNegativityPlayer(p).fight();
					SpigotNegativityPlayer.getNegativityPlayer(pl).fight();
				}
		
	}
}
