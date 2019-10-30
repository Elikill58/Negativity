package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class EditedClientProtocol extends Cheat {

	public EditedClientProtocol() {
		super("EDITED_CLIENT", true, ItemTypes.FEATHER, false, false, "hacked client", "edited client");
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
