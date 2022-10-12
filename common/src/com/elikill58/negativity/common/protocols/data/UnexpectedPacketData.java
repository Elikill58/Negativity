package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class UnexpectedPacketData extends CheckData {

	public long vehicleLeft = 0;
	public boolean alreadySend = false;
	
	public UnexpectedPacketData(NegativityPlayer np) {
		super(np);
	}
}
