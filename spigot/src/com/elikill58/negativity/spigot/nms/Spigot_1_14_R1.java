package com.elikill58.negativity.spigot.nms;

public class Spigot_1_14_R1 extends NoRemapSpigotVersionAdapter {

	public Spigot_1_14_R1() {
		super("v1_14_R1");
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public String getTpsFieldName() {
		return "f";
	}
}
