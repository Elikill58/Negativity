package com.elikill58.negativity.api.potion;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.universal.Adapter;

public enum PotionEffectType {
	
	BLINDNESS,
	CONFUSION,
	DAMAGE_RESISTANCE,
	DOLPHINS_GRACE,
	INCREASE_DAMAGE,
	INSTANT_HEAL,
	FAST_DIGGING,
	HUNGER,
	JUMP,
	LEVITATION,
	NIGHT_VISION,
	POISON,
	REGENERATION("HEALTH_BOOST"),
	SLOW_DIGGING,
	SPEED,
	WEAKNESS,
	WITHER,
	UNKNOW;
	
	private final List<String> alias;
	
	private PotionEffectType(String... alias) {
		this.alias = Arrays.asList(alias);
	}
	
	/**
	 * Get all other names of the potion effect (for compatibility with multiples versions)
	 * All of them are hardcoded in upper case.
	 * 
	 * @return list of all names
	 */
	public List<String> getAlias() {
		return alias;
	}
	
	public static PotionEffectType fromName(String name) {
		for(PotionEffectType type : values())
			if(type.name().equalsIgnoreCase(name) || type.getAlias().contains(name.toUpperCase()))
				return type;
		Adapter.getAdapter().getLogger().warn("[PotionEffectType] Cannot found effect " + name + " !");
		return UNKNOW;
	}
}
