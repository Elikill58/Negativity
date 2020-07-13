package com.elikill58.negativity.common.potion;

public enum PotionEffectType {
	
	SPEED;
	
	public static PotionEffectType fromName(String name) {
		for(PotionEffectType type : values())
			if(type.name().equalsIgnoreCase(name))
				return type;
		return null;
	}
}
