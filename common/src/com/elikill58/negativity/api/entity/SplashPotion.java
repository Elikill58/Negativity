package com.elikill58.negativity.api.entity;

import java.util.List;

import com.elikill58.negativity.api.potion.PotionEffect;

public interface SplashPotion extends Entity {

	List<PotionEffect> getEffects();
	
	@Override
	default EntityType getType() {
		return EntityType.SPLASH_POTION;
	}
}
