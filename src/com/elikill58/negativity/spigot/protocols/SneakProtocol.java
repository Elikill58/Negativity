package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;

public class SneakProtocol extends Cheat {

	public SneakProtocol() {
		super(CheatKeys.SNEAK, true, Material.BLAZE_POWDER, CheatCategory.MOVEMENT, false, "sneack");
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
