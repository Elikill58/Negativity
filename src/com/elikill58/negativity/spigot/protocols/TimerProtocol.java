package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class TimerProtocol extends Cheat {

	public TimerProtocol() {
		super("TIMER", true, Material.FEATHER, false, false, "hacked client", "edited client");
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
