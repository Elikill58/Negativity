package com.elikill58.negativity.api;

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
	
	public static GameMode get(String name) {
		for(GameMode gm : GameMode.values())
			if(gm.getName().equalsIgnoreCase(name) || gm.name().equalsIgnoreCase(name))
				return gm;
		return null;
	}
}
