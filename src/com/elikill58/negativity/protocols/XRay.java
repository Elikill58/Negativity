package com.elikill58.negativity.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;

public class XRay extends Cheat {
	
	public XRay() {
		super(CheatKeys.XRAY, false, Material.EMERALD_ORE, CheatCategory.WORLD, false);
	}
}
