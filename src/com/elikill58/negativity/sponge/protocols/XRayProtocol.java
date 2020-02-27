package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;

public class XRayProtocol extends Cheat {
	
	public XRayProtocol() {
		super(CheatKeys.XRAY, false, ItemTypes.EMERALD_ORE, CheatCategory.WORLD, false);
	}
}
