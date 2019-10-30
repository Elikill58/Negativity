package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class AllProtocol extends Cheat {

	public AllProtocol() {
		super("ALL", true, ItemTypes.GRASS, false, false);
	}

	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
