package com.elikill58.negativity.fabric.impl;

import com.elikill58.negativity.api.potion.PotionEffectType;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

public class FabricPotionEffectType {
	
	public static StatusEffect getEffect(com.elikill58.negativity.api.potion.PotionEffectType type) {
		switch (type) {
		case BLINDNESS:
			return StatusEffects.BLINDNESS;
		case NAUSEA:
			return StatusEffects.NAUSEA;
		case DOLPHINS_GRACE:
			return StatusEffects.DOLPHINS_GRACE;
		case HASTE:
			return StatusEffects.HASTE;
		case HUNGER:
			return StatusEffects.HUNGER;
		case INSTANT_HEAL:
			return StatusEffects.INSTANT_HEALTH;
		case JUMP:
			return StatusEffects.JUMP_BOOST;
		case LEVITATION:
			return StatusEffects.LEVITATION;
		case NIGHT_VISION:
			return StatusEffects.NIGHT_VISION;
		case POISON:
			return StatusEffects.POISON;
		case REGENERATION:
			return StatusEffects.REGENERATION;
		case SLOW_MINING:
			return StatusEffects.MINING_FATIGUE;
		case SPEED:
			return StatusEffects.SPEED;
		case WEAKNESS:
			return StatusEffects.WEAKNESS;
		case WITHER:
			return StatusEffects.WITHER;
		default:
			return null;
		}
	}
	
	public static com.elikill58.negativity.api.potion.PotionEffectType getEffect(StatusEffect effect) {
		for(PotionEffectType type : PotionEffectType.values()) {
			if(effect.getTranslationKey().toUpperCase().contains(type.name()))
				return type;
		}
		return null;
	}
}
