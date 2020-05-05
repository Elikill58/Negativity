package com.elikill58.negativity.sponge.support;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class EssentialsSupport {

	

	
	public static float getEssentialsRealMoveSpeed(Player p) {
        final float defaultSpeed = p.get(Keys.IS_FLYING).orElse(false) ? 0.1f : 0.2f;
        float maxSpeed = 1f, walkSpeed = (float) (double) p.get(Keys.WALKING_SPEED).get();
        if (walkSpeed < 1f)
            return (float) (defaultSpeed * walkSpeed);
        else
            return (float) (((walkSpeed - 1) / 9) * (maxSpeed - defaultSpeed) + defaultSpeed);
    }
}
