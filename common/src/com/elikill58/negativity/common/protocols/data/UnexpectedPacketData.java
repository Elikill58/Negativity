package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class UnexpectedPacketData extends CheckData {

	public long vehicleLeft = 0;
	public int lastSlot = -1, timeSlot = 0;
	
	public UnexpectedPacketData(NegativityPlayer np) {
		super(np);
	}
}
