package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class SneakProtocol extends Cheat {

	public SneakProtocol() {
		super("SNEAK", true, ItemTypes.BLAZE_POWDER, false, false, "sneack");
	}

	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
