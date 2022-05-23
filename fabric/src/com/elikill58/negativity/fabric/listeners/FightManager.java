package com.elikill58.negativity.fabric.listeners;

public class FightManager {

	// TODO re-add all of the fight system
	/*@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e, @First Player p) {
		if (!(e.getTargetEntity() instanceof Player))
			return;
		NegativityPlayer.getCached(e.getTargetEntity().getUniqueId()).fight();
		NegativityPlayer.getCached(p.getUniqueId()).fight();
	}

	@Listener
	public void onProjectileHit(DamageEntityEvent e) {
		if (!e.getTargetEntity().getType().equals(EntityTypes.SPLASH_POTION))
			return;
		Location<?> loc = e.getTargetEntity().getLocation();
		PotionEffect po = (PotionEffect) e.getTargetEntity();
		PotionEffectType pe = po.getType();
		if (pe == PotionEffectTypes.INSTANT_DAMAGE || pe == PotionEffectTypes.POISON
				|| pe == PotionEffectTypes.SLOWNESS || pe == PotionEffectTypes.WEAKNESS
				|| pe == PotionEffectTypes.FIRE_RESISTANCE || pe == PotionEffectTypes.INSTANT_HEALTH
				|| pe == PotionEffectTypes.REGENERATION || pe == PotionEffectTypes.STRENGTH
				|| pe == PotionEffectTypes.SPEED)
			for (Player p : Sponge.getServer().getOnlinePlayers())
				if (loc.getExtent().equals(p.getLocation().getExtent()))
					if (p.getPosition().distance(loc.getPosition()) < 9)
						NegativityPlayer.getCached(p.getUniqueId()).fight();
	}
	
	@Listener
	public void onEntityExplode(ExplosionEvent e) {
		Sponge.getServer().getOnlinePlayers().stream().filter((p) -> p.getPosition().distance(e.getExplosion().getLocation().getPosition()) < 5)
				.forEach((p) -> NegativityPlayer.getCached(p.getUniqueId()).fight());
	}*/
}
