package com.elikill58.negativity.minestom.impl.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.minestom.impl.FabricPotionEffectType;

import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.potion.PotionUtil;

public class MinestomSplashPotion extends MinestomEntity<PotionEntity> implements SplashPotion {
	
	public MinestomSplashPotion(PotionEntity entity) {
		super(entity);
	}

	@Override
	public List<PotionEffect> getEffects() {
		return PotionUtil.getPotionEffects(entity.getStack()).stream().map(effect -> new PotionEffect(FabricPotionEffectType.getEffect(effect.getEffectType()), effect.getDuration(), effect.getAmplifier())).collect(Collectors.toList());
	}
}
