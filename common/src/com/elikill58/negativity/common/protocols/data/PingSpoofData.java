package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class PingSpoofData extends CheckData {

	public long pingId = 0, pingTime = 0;
	
	public PingSpoofData(NegativityPlayer np) {
		super(np);
	}
}
