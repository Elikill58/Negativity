package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;
import com.elikill58.negativity.common.protocols.SuperKnockback.PacketWaiting;

public class SuperKnockbackData extends CheckData {

	public long actionSneak = 0, timeStop = 0, timeFirstStart = 0, diffAction = 0;
	public PacketWaiting waiting = PacketWaiting.NOTHING;
	
	public SuperKnockbackData(NegativityPlayer np) {
		super(np);
	}
}
