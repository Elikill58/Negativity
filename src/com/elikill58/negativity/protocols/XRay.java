package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;

public class XRay extends Cheat {
	
	public XRay() {
		super(CheatKeys.XRAY, false, Materials.EMERALD_ORE, CheatCategory.WORLD, false);
	}
}
