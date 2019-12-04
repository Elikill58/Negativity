package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class TimerProtocol extends Cheat {

	public TimerProtocol() {
		super("TIMER", true, ItemTypes.FEATHER, false, false, "hacked client", "edited client");
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
