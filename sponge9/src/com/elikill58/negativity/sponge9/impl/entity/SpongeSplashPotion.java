package com.elikill58.negativity.sponge9.impl.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.projectile.Potion;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.sponge9.utils.Utils;

public class SpongeSplashPotion extends SpongeEntity<Potion> implements SplashPotion {
	
	public SpongeSplashPotion(Potion po) {
		super(po);
	}

	@Override
	public List<PotionEffect> getEffects() {
		return entity.get(Keys.POTION_EFFECTS).get().stream().map(this::createPotionEffect).collect(Collectors.toList());
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(Utils.getKey(effect.type()).asString()), (int) effect.duration().ticks(), effect.amplifier());
	}
}
