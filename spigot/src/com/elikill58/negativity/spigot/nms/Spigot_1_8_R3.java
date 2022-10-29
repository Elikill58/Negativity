package com.elikill58.negativity.spigot.nms;

import org.bukkit.OfflinePlayer;

import com.elikill58.negativity.spigot.utils.Utils;

public class Spigot_1_8_R3 extends NoRemapSpigotVersionAdapter {

	public Spigot_1_8_R3() {
		super("v1_8_R3");
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public String getTpsFieldName() {
		return "h";
	}
	
	@Override
	public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
		return Utils.createSkullOldVersion(owner);
	}
}
