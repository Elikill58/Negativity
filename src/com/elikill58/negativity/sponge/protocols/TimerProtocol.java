package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;

public class TimerProtocol extends Cheat {

	public TimerProtocol() {
		super(CheatKeys.TIMER, true, ItemTypes.CLOCK, CheatCategory.MOVEMENT, true);
	}
}
