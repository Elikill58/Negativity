package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;

public class XRayProtocol extends Cheat {
	
	public XRayProtocol() {
		super(CheatKeys.XRAY, false, Material.EMERALD_ORE, CheatCategory.WORLD, false);
	}
}
