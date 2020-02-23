package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;

public class FightManager {

	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		if (!(p instanceof Player) || !(e.getTargetEntity() instanceof Player))
			return;
		SpongeNegativityPlayer.getNegativityPlayer((Player) e.getTargetEntity()).fight();
		SpongeNegativityPlayer.getNegativityPlayer(p).fight();
	}

	@Listener
	public void onProjectileHit(DamageEntityEvent e) {
		if (!e.getTargetEntity().getType().equals(EntityTypes.SPLASH_POTION))
			return;
		Location<?> loc = e.getTargetEntity().getLocation();
		PotionEffect po = (PotionEffect) e.getTargetEntity();
		PotionEffectType pe = po.getType();
		if (pe == PotionEffectTypes.INSTANT_DAMAGE || pe == PotionEffectTypes.POISON
				|| pe == PotionEffectTypes.SLOWNESS || pe == PotionEffectTypes.WEAKNESS || pe == PotionEffectTypes.POISON
				|| pe == PotionEffectTypes.FIRE_RESISTANCE || pe == PotionEffectTypes.INSTANT_HEALTH || pe == PotionEffectTypes.REGENERATION
				|| pe == PotionEffectTypes.STRENGTH || pe == PotionEffectTypes.SPEED)
			for (Player p : Utils.getOnlinePlayers())
				if (((World) loc.getExtent()).equals(p.getLocation().getExtent()))
					if (p.getPosition().distance(loc.getPosition()) < 9)
						SpongeNegativityPlayer.getNegativityPlayer(p).fight();
	}

	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death e, @First Player p) {
		SpongeNegativityPlayer.getNegativityPlayer(p).unfight();
	}
	
	@Listener
	public void onEntityExplode(ExplosionEvent e) {
		Sponge.getServer().getOnlinePlayers().stream().filter((p) -> p.getPosition().distance(e.getExplosion().getLocation().getPosition()) < 5)
				.forEach((p) -> SpongeNegativityPlayer.getNegativityPlayer(p).fight());
	}
}
