package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.protocols.CheckData;

public class AutoStealData extends CheckData {

	public long invClick = 0;
	public boolean invWas = false;
	public int invSlot = 0;
	public Material invItem = null;
	
	public AutoStealData(NegativityPlayer np) {
		super(np);
	}
}
