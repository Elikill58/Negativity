package com.elikill58.negativity.api.potion;

public class PotionEffect implements Cloneable {
	
	private final PotionEffectType type;
	private final int duration, amplifier;
	
	/**
	 * Create a new potion effect.
	 * It will be active for ever, without any amplifier (so level 1)
	 * 
	 * @param type the type of the potion effect
	 */
	public PotionEffect(PotionEffectType type) {
		this(type, Integer.MAX_VALUE, 0);
	}
	
	/**
	 * Create a new option effect.
	 * 
	 * @param type the type of the potion effect
	 * @param duration the duration (in seconds) of the effect
	 * @param amplifier the amplifier of the potion effect
	 */
	public PotionEffect(PotionEffectType type, int duration, int amplifier) {
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	/**
	 * Get the potion effect type
	 * 
	 * @return the type of the effect
	 */
	public PotionEffectType getType() {
		return type;
	}

	/**
	 * Get the duration of the potion effect
	 * 
	 * @return the effect duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Get the amplifier of the potion effect
	 * By default, it's 0 to make the level to 1.
	 * 
	 * @return the amplifier
	 */
	public int getAmplifier() {
		return amplifier;
	}
	
	@Override
	public PotionEffect clone() {
		try {
			return (PotionEffect) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
