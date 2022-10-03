package com.elikill58.negativity.api.inventory;

public enum Hand {
	MAIN,
	OFF;
	
	public static Hand getHand(String name) {
		for(Hand h : values()) {
			if(h.name().equalsIgnoreCase(name) || h.name().contains(name.toUpperCase())) {
				return h;
			}
		}
		return MAIN;
	}
}
