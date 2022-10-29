package com.elikill58.negativity.spigot.nms;

public class Spigot_1_16_R3 extends NoRemapSpigotVersionAdapter {
	
	public Spigot_1_16_R3() {
		super("v1_16_R3");
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public String getTpsFieldName() {
		return "h";
	}
}
