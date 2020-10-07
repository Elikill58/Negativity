package com.elikill58.negativity.universal.support;

import com.earth2me.essentials.Essentials;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;

public class EssentialsSupport {

	private static Essentials essentials = (Essentials) Adapter.getAdapter().getPlugin("Essentials").getDefault();
	
	public static boolean checkEssentialsPrecondition(Player p) {
		return essentials.getUser(p.getUniqueId()).isGodModeEnabled();
	}
	
	public static boolean checkEssentialsSpeedPrecondition(Player p) {
		if(p.hasPermission("essentials.speed"))
			return true;
		else
			return false;
	}
	
	public static float getEssentialsRealMoveSpeed(Player p) {
        final float defaultSpeed = p.isFlying() ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (p.getWalkSpeed() < 1f)
            return defaultSpeed * p.getWalkSpeed();
        else
            return ((p.getWalkSpeed() - 1) / 9) * (maxSpeed - defaultSpeed) + defaultSpeed;
    }
}
