package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class XRayProtocol extends Cheat {
	
	public XRayProtocol() {
		super("XRAY", false, Material.EMERALD_ORE, false, false);
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
