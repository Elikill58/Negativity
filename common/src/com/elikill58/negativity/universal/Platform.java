package com.elikill58.negativity.universal;

public enum Platform {
	
	BUNGEE("bungee"),
	SPIGOT("spigot"),
	SPONGE("sponge"),
	VELOCITY("velocity");
	
	private final String name;
	
	private Platform(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
