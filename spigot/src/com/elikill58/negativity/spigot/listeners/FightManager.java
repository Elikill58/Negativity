package com.elikill58.negativity.spigot.listeners;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

@SuppressWarnings("deprecation")
public class FightManager implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		// Ignoring Citizen NPC
		if(e.getDamager().hasMetadata("NPC") || e.getEntity().hasMetadata("NPC"))
			return;
		if(e.getDamager() instanceof Player)
			NegativityPlayer.getCached(e.getDamager().getUniqueId()).fight();
		if(e.getEntity() instanceof Player)
			NegativityPlayer.getCached(e.getEntity().getUniqueId()).fight();
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC"))
			NegativityPlayer.getCached(e.getEntity().getUniqueId()).fight();
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
		String name = e.getPlayer().getItemInHand().getType().name();
		if(name.contains("SWORD") || name.contains("AXE") || name.contains("APPLE") || name.contains("BOW") || name.contains("POTION"))
			manageFightBetweenTwoPlayers(e.getPlayer(), 15);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		Projectile hittingEntity = e.getEntity();
		if (!(hittingEntity instanceof ThrownPotion)) {
			return;
		}
		
		Collection<PotionEffect> effects = ((ThrownPotion) hittingEntity).getEffects();
		Location loc = hittingEntity.getLocation();
		for (PotionEffect effect : effects) {
			PotionEffectType type = effect.getType();
			if (isPositiveFightEffect(type)) {
				for (Player p : loc.getWorld().getPlayers()) {
					if (loc.distance(p.getLocation()) < 18 && loc.distance(p.getLocation()) > 4)
						NegativityPlayer.getCached(p.getUniqueId()).fight();
				}
			} else if (isNegativeFightEffect(type)) {
				for (Player p : loc.getWorld().getPlayers()) {
					if (loc.distance(p.getLocation()) < 9)
						NegativityPlayer.getCached(p.getUniqueId()).fight();
				}
			}
		}
	}
	
	private boolean isNegativeFightEffect(PotionEffectType type) {
		return PotionEffectType.HARM.equals(type) || PotionEffectType.POISON.equals(type)
			|| PotionEffectType.WEAKNESS.equals(type) || PotionEffectType.SLOW.equals(type);
	}
	
	private boolean isPositiveFightEffect(PotionEffectType type) {
		return PotionEffectType.FIRE_RESISTANCE.equals(type) || PotionEffectType.HEAL.equals(type)
			|| PotionEffectType.REGENERATION.equals(type) || PotionEffectType.SPEED.equals(type)
			|| PotionEffectType.INCREASE_DAMAGE.equals(type);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		NegativityPlayer.getCached(e.getEntity().getUniqueId()).unfight();
	}
	
	@EventHandler
	public void blowUp(EntityExplodeEvent e) {
		if(!e.getEntityType().equals(EntityType.PRIMED_TNT))
			return;
		Location loc = e.getLocation();
		Utils.getOnlinePlayers().stream().filter((p) -> loc.getWorld().equals(p.getWorld()) && loc.distance(p.getLocation()) < 5).forEach((p) -> NegativityPlayer.getCached(p.getUniqueId()).fight());
	}
	
	private void manageFightBetweenTwoPlayers(Player p, int maxDistance) {
		if(!p.getWorld().getPVP())
			return;
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		for(Player pl : Utils.getOnlinePlayers()) {
			NegativityPlayer npOther = NegativityPlayer.getCached(p.getUniqueId());
			if(npOther.isInFight && np.isInFight)
				continue;
			if(pl.getLocation().getWorld().equals(p.getLocation().getWorld())) {
				if(pl.getLocation().distance(p.getLocation()) < maxDistance) {
					np.fight();
					npOther.fight();
				}
			}
		}
	}
}
