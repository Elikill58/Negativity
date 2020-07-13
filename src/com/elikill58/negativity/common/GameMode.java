package com.elikill58.negativity.common;

public enum GameMode {
	
	SURVIVAL("Survival"),
	ADVENTURE("Adventure"),
	CREATIVE("Creative"),
	SPECTATOR("Spectator");
	
	private final String name;
	
	private GameMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
