package com.elikill58.negativity.spigot.nms;

public class Spigot_1_13_R2 extends NoRemapSpigotVersionAdapter {

	public Spigot_1_13_R2() {
		super("v1_13_R2");
		
		log();
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public String getTpsFieldName() {
		return "d";
	}
}
