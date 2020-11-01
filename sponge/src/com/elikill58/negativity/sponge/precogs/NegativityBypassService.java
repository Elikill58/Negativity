package com.elikill58.negativity.sponge.precogs;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import com.me4502.precogs.detection.DetectionType;
import com.me4502.precogs.service.AntiCheatService;
import com.me4502.precogs.service.BypassTicket;

public class NegativityBypassService implements AntiCheatService {

	@Override
	public double getViolationLevel(User arg0, DetectionType arg1) {
		return 0;
	}

	@Override
	public void logViolation(User arg0, DetectionType arg1, double arg2) {
		
	}

	@Override
	public void logViolation(User arg0, DetectionType arg1, double arg2, String arg3) {
		
	}

	@Override
	public Optional<BypassTicket> requestBypassTicket(Player p, List<DetectionType> detections, Object owner) {
		return Optional.of(new NegativityBypassTicket(p, detections, owner));
	}

}
