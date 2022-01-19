package com.elikill58.negativity.api.potion;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.universal.Adapter;

public enum PotionEffectType {

	SPEED((byte) 1, "minecraft:speed"),
	SLOWNESS((byte) 2, "minecraft:slowness"),
	HASTE((byte) 3, "minecraft:haste"),
	SLOW_MINING((byte) 4, "minecraft:mining_fatigue"),
	STRENGTH((byte) 5, "minecraft:strength", "INCREASE_DAMAGE"),
	INSTANT_HEAL((byte) 6, "minecraft:instant_health", "HEAL"),
	INSTANT_DAMAGE((byte) 7, "minecraft:instant_damage"),
	JUMP((byte) 8, "minecraft:jump_boost"),
	NAUSEA((byte) 9, "minecraft:nausea"),
	REGENERATION((byte) 10, "minecraft:regeneration"),
	RESISTANCE((byte) 11, "minecraft:resistance"),
	FIRE_RESISTANCE((byte) 12, "minecraft:fire_resistance"),
	WATER_BREATHING((byte) 13, "minecraft:water_breathing"),
	INVISIBILITY((byte) 14, "minecraft:invisibility"),
	BLINDNESS((byte) 15, "minecraft:blindness"),
	NIGHT_VISION((byte) 16, "minecraft:night_vision"),
	HUNGER((byte) 17, "minecraft:hunger"),
	WEAKNESS((byte) 18, "minecraft:weakness"),
	POISON((byte) 19, "minecraft:poison"),
	WITHER((byte) 20, "minecraft:wither"),
	HEALTH_BOOST((byte) 21, "minecraft:health_boost"),
	ABSORPTION((byte) 22, "minecraft:absorption"),
	SATURATION((byte) 23, "minecraft:saturation"),
	GLOWING((byte) 24, "minecraft:glowing"),
	LEVITATION((byte) 25, "minecraft:levitation"),
	LUCK((byte) 26, "minecraft:luck"),
	UNLUCK((byte) 27, "minecraft:unluck"),
	SLOW_FALLING((byte) 28, "minecraft:slow_falling"),
	CONDUIT_POWER((byte) 29, "minecraft:conduit_power"),
	DOLPHINS_GRACE((byte) 30, "minecraft:dolphins_grace"),
	BAD_OMEN((byte) 31, "minecraft:bad_omen"),
	HERO_OF_THE_VILLAGE((byte) 32, "minecraft:hero_of_the_village"),
	UNKNOW((byte) -1, "minecraft:unknown");
	
	private final byte bId;
	private final String id;
	private final List<String> alias;
	
	PotionEffectType(byte bId, String id, String... alias) {
		this.bId = bId;
		this.id = id;
		this.alias = Arrays.asList(alias);
	}
	
	public byte getByteId() {
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
	
	public static PotionEffectType fromByte(byte b) {
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
