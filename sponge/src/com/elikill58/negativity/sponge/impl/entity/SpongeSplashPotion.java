package com.elikill58.negativity.sponge.impl.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.projectile.ThrownPotion;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;

public class SpongeSplashPotion extends SpongeEntity<ThrownPotion> implements SplashPotion {
	
	public SpongeSplashPotion(ThrownPotion po) {
		super(po);
	}

	@Override
	public List<PotionEffect> getEffects() {
		return entity.get(Keys.POTION_EFFECTS).get().stream().map(this::createPotionEffect).collect(Collectors.toList());
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(effect.getType().getId()), effect.getDuration(), effect.getAmplifier());
	}
}
