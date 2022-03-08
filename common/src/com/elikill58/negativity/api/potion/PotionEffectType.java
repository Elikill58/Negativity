package com.elikill58.negativity.api.potion;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.universal.Adapter;

public enum PotionEffectType {

	SPEED(1, "minecraft:speed"),
	SLOWNESS(2, "minecraft:slowness"),
	HASTE(3, "minecraft:haste"),
	SLOW_MINING(4, "minecraft:mining_fatigue"),
	STRENGTH(5, "minecraft:strength", "INCREASE_DAMAGE"),
	INSTANT_HEAL(6, "minecraft:instant_health", "HEAL"),
	INSTANT_DAMAGE(7, "minecraft:instant_damage"),
	JUMP(8, "minecraft:jump_boost"),
	NAUSEA(9, "minecraft:nausea"),
	REGENERATION(10, "minecraft:regeneration"),
	RESISTANCE(11, "minecraft:resistance"),
	FIRE_RESISTANCE(12, "minecraft:fire_resistance"),
	WATER_BREATHING(13, "minecraft:water_breathing"),
	INVISIBILITY(14, "minecraft:invisibility"),
	BLINDNESS(15, "minecraft:blindness"),
	NIGHT_VISION(16, "minecraft:night_vision"),
	HUNGER(17, "minecraft:hunger"),
	WEAKNESS(18, "minecraft:weakness"),
	POISON(19, "minecraft:poison"),
	WITHER(20, "minecraft:wither"),
	HEALTH_BOOST(21, "minecraft:health_boost"),
	ABSORPTION(22, "minecraft:absorption"),
	SATURATION(23, "minecraft:saturation"),
	GLOWING(24, "minecraft:glowing"),
	LEVITATION(25, "minecraft:levitation"),
	LUCK(26, "minecraft:luck"),
	UNLUCK(27, "minecraft:unluck"),
	SLOW_FALLING(28, "minecraft:slow_falling"),
	CONDUIT_POWER(29, "minecraft:conduit_power"),
	DOLPHINS_GRACE(30, "minecraft:dolphins_grace"),
	BAD_OMEN(31, "minecraft:bad_omen"),
	HERO_OF_THE_VILLAGE(32, "minecraft:hero_of_the_village"),
	UNKNOW(-1, "minecraft:unknown");
	
	private final int bId;
	private final String id;
	private final List<String> alias;
	
	PotionEffectType(int bId, String id, String... alias) {
		this.bId = bId;
		this.id = id;
		this.alias = Arrays.asList(alias);
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
