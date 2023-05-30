package com.elikill58.negativity.common.protocols.data;

import java.util.Optional;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.CheckData;

public class SpeedData extends CheckData {

	public int oldSpeedLevel = 0, oldSlowLevel = 0, highSpeedAmount = 0;
	public double sameDiffY = 0, oldFriction = 0, walkSpeedBuffer = 0, deltaXZ = 0, distanceJumpingBuffer = 0;
	
	public SpeedData(NegativityPlayer np) {
		super(np);
	}
	
	public double getSpeedModifier() {
		Optional<PotionEffect> optSpeed = np.getPlayer().getPotionEffect(PotionEffectType.SPEED);
		if (optSpeed.isPresent()) {
			int amplifierSpeed = optSpeed.get().getAmplifier() + 1;
			oldSpeedLevel = amplifierSpeed;
			return 1d + (double) amplifierSpeed * 0.2;
		} else if (oldSpeedLevel > 0) {
			return 1d + (double) oldSpeedLevel-- * 0.2;
		}
		return 1;
	}
	
	public double getSlowModifier() {
		Optional<PotionEffect> optSlow = np.getPlayer().getPotionEffect(PotionEffectType.SLOWNESS);
		if (optSlow.isPresent()) {
			int amplifierSlow = optSlow.get().getAmplifier();
			oldSlowLevel = amplifierSlow;
			return (double) amplifierSlow * 0.15;
		} else if (oldSlowLevel > 0) {
			return (double) oldSlowLevel-- * 0.15;
		}
		return 1;
	}
	
	public void reduceWalkSpeedBuffer(double r) {
		walkSpeedBuffer -= r;
    	if(walkSpeedBuffer < 0) {
    		walkSpeedBuffer = 0;
    	}
	}
	
	public void reduceDistanceJumpingBuffer(double r) {
		distanceJumpingBuffer -= r;
    	if(distanceJumpingBuffer < 0) {
    		distanceJumpingBuffer = 0;
    	}
	}
}
