package com.elikill58.negativity.api.potion;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.universal.Adapter;

public enum PotionEffectType {

	SPEED(PotionEffectBehavior.POSITIVE, 1, "minecraft:speed"),
	SLOWNESS(PotionEffectBehavior.NEGATIVE, 2, "minecraft:slowness"),
	HASTE(PotionEffectBehavior.POSITIVE, 3, "minecraft:haste"),
	SLOW_MINING(PotionEffectBehavior.NEGATIVE, 4, "minecraft:mining_fatigue"),
	STRENGTH(PotionEffectBehavior.POSITIVE, 5, "minecraft:strength", "INCREASE_DAMAGE"),
	INSTANT_HEAL(PotionEffectBehavior.POSITIVE, 6, "minecraft:instant_health", "HEAL"),
	INSTANT_DAMAGE(PotionEffectBehavior.NEGATIVE, 7, "minecraft:instant_damage"),
	JUMP(PotionEffectBehavior.POSITIVE, 8, "minecraft:jump_boost"),
	NAUSEA(PotionEffectBehavior.NEGATIVE, 9, "minecraft:nausea"),
	REGENERATION(PotionEffectBehavior.POSITIVE, 10, "minecraft:regeneration"),
	RESISTANCE(PotionEffectBehavior.POSITIVE, 11, "minecraft:resistance", "DAMAGE_RESISTANCE"),
	FIRE_RESISTANCE(PotionEffectBehavior.POSITIVE, 12, "minecraft:fire_resistance"),
	WATER_BREATHING(PotionEffectBehavior.POSITIVE, 13, "minecraft:water_breathing"),
	INVISIBILITY(PotionEffectBehavior.POSITIVE, 14, "minecraft:invisibility"),
	BLINDNESS(PotionEffectBehavior.NEGATIVE, 15, "minecraft:blindness"),
	NIGHT_VISION(PotionEffectBehavior.POSITIVE, 16, "minecraft:night_vision"),
	HUNGER(PotionEffectBehavior.NEGATIVE, 17, "minecraft:hunger"),
	WEAKNESS(PotionEffectBehavior.NEGATIVE, 18, "minecraft:weakness"),
	POISON(PotionEffectBehavior.NEGATIVE, 19, "minecraft:poison"),
	WITHER(PotionEffectBehavior.NEGATIVE, 20, "minecraft:wither"),
	HEALTH_BOOST(PotionEffectBehavior.POSITIVE, 21, "minecraft:health_boost"),
	ABSORPTION(PotionEffectBehavior.POSITIVE, 22, "minecraft:absorption"),
	SATURATION(PotionEffectBehavior.POSITIVE, 23, "minecraft:saturation"),
	GLOWING(PotionEffectBehavior.NEUTRAL, 24, "minecraft:glowing"),
	LEVITATION(PotionEffectBehavior.NEGATIVE, 25, "minecraft:levitation"),
	FATAL_POISON(PotionEffectBehavior.NEGATIVE, -1, "minecraft:fatal_poison"), // bedrock only
	LUCK(PotionEffectBehavior.POSITIVE, 26, "minecraft:luck"),
	UNLUCK(PotionEffectBehavior.NEGATIVE, 27, "minecraft:unluck"),
	SLOW_FALLING(PotionEffectBehavior.POSITIVE, 28, "minecraft:slow_falling"),
	CONDUIT_POWER(PotionEffectBehavior.POSITIVE, 29, "minecraft:conduit_power"),
	DOLPHINS_GRACE(PotionEffectBehavior.POSITIVE, 30, "minecraft:dolphins_grace"),
	BAD_OMEN(PotionEffectBehavior.NEUTRAL, 31, "minecraft:bad_omen"),
	HERO_OF_THE_VILLAGE(PotionEffectBehavior.POSITIVE, 32, "minecraft:hero_of_the_village", "minecraft:village_hero"),
	DARKNESS(PotionEffectBehavior.NEGATIVE, 33, "minecraft:darkenss"),
	UNKNOW(PotionEffectBehavior.NEUTRAL, -1, "minecraft:unknown");
	
	private final PotionEffectBehavior behavior;
	private final int bId;
	private final String id;
	private final List<String> alias;
	
	PotionEffectType(PotionEffectBehavior behavior, int bId, String id, String... alias) {
		this.behavior = behavior;
		this.bId = bId;
		this.id = id;
		this.alias = Arrays.asList(alias);
	}
	
	public PotionEffectBehavior getBehavior() {
		return behavior;
	}
	
	public int getByteId() {
		return bId;
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
			if (type.getId().equalsIgnoreCase(id)) {
				return type;
			}
		}
		return UNKNOW;
	}
	
	public static PotionEffectType fromId(int b) {
		for (PotionEffectType type : values()) {
			if (type.getByteId() == b) {
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
