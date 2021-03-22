package com.elikill58.negativity.sponge8.listeners;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Potion;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.elikill58.negativity.api.NegativityPlayer;

public class FightManager {
	
	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First ServerPlayer attacker, @Getter("entity") ServerPlayer attacked) {
		NegativityPlayer.getCached(attacked.uniqueId()).fight();
		NegativityPlayer.getCached(attacker.uniqueId()).fight();
	}
	
	@Listener
	public void onProjectileHit(DamageEntityEvent e, @Getter("entity") Potion potion) {
		for (PotionEffect effect : potion.require(Keys.POTION_EFFECTS)) {
			PotionEffectType type = effect.type();
			if (type == PotionEffectTypes.INSTANT_DAMAGE.get() || type == PotionEffectTypes.POISON.get()
				|| type == PotionEffectTypes.SLOWNESS.get() || type == PotionEffectTypes.WEAKNESS.get()
				|| type == PotionEffectTypes.FIRE_RESISTANCE.get() || type == PotionEffectTypes.INSTANT_HEALTH.get()
				|| type == PotionEffectTypes.REGENERATION.get() || type == PotionEffectTypes.STRENGTH.get()
				|| type == PotionEffectTypes.SPEED.get()) {
				for (Entity hitPlayer : potion.nearbyEntities(9, entity -> entity instanceof Player)) {
					NegativityPlayer.getCached(hitPlayer.uniqueId()).fight();
				}
			}
		}
	}
	
	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death e, @First ServerPlayer p) {
		NegativityPlayer.getCached(p.uniqueId()).unfight();
	}
	
	@Listener
	public void onEntityExplode(ExplosionEvent.Detonate e) {
		for (Entity entity : e.entities()) {
			if (entity instanceof Player) {
				NegativityPlayer.getCached(entity.uniqueId()).fight();
			}
		}
	}
}
