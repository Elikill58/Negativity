package com.elikill58.negativity.api.potion;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.universal.Adapter;

public enum PotionEffectType {
	
	BAD_OMEN("minecraft:bad_omen"),
	BLINDNESS("minecraft:blindness"),
	CONDUIT_POWER("minecraft:conduit_power"),
	CONFUSION("minecraft:nausea"),
	DAMAGE_RESISTANCE("minecraft:resistance"),
	DOLPHINS_GRACE("minecraft:dolphins_grace"),
	INCREASE_DAMAGE("minecraft:strength"),
	INSTANT_HEAL("minecraft:instant_health", "HEAL"),
	FAST_DIGGING("minecraft:haste"),
	FIRE_RESISTANCE("minecraft:fire_resistance"),
	HUNGER("minecraft:hunger"),
	JUMP("minecraft:jump_boost"),
	LEVITATION("minecraft:levitation"),
	NIGHT_VISION("minecraft:night_vision"),
	POISON("minecraft:poison"),
	REGENERATION("minecraft:regeneration", "HEALTH_BOOST"),
	SLOW("minecraft:slowness"),
	SLOW_DIGGING("minecraft:mining_fatigue"),
	SPEED("minecraft:speed"),
	WATER_BREATHING("minecraft:water_breathing"),
	WEAKNESS("minecraft:weakness"),
	WITHER("minecraft:wither"),
	UNKNOW("minecraft:unknown");
	
	private final String id;
	private final List<String> alias;
	
	PotionEffectType(String id, String... alias) {
		this.id = id;
		this.alias = Arrays.asList(alias);
	}
	
	public String getId() {
		return id;
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
	
	public static PotionEffectType forId(String id) {
		for (PotionEffectType type : values()) {
			if (type.id.equalsIgnoreCase(id)) {
				return type;
			}
		}
		return UNKNOW;
	}
	
	public static PotionEffectType fromName(String name) {
		for(PotionEffectType type : values())
			if(type.id.equalsIgnoreCase(name) || type.name().equalsIgnoreCase(name) || type.getAlias().contains(name.toUpperCase(Locale.ROOT)))
				return type;
		Adapter.getAdapter().debug("[PotionEffectType] Cannot found effect " + name + " !");
		return UNKNOW;
	}
}
