package com.elikill58.negativity.common.potion;

public class PotionEffect {
	
	private final PotionEffectType type;
	
	public PotionEffect(PotionEffectType type) {
		this.type = type;
	}

	public PotionEffectType getType() {
		return type;
	}

}
