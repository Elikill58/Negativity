package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class AllProtocol extends Cheat {

	public AllProtocol() {
		super("ALL", true, Material.GRASS, false, false);
	}

	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
