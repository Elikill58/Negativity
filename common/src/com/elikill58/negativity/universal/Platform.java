package com.elikill58.negativity.universal;

public enum Platform {

	BUNGEE("bungee", true),
	FABRIC("fabric", false),
	MINESTOM("minestom", false),
	SPIGOT("spigot", false),
	SPONGE("sponge", false),
	SPONGE8("sponge8", false),
	SPONGE9("sponge9", false),
	VELOCITY("velocity", true);
	
	private final String name;
	private final boolean proxy;
	
	Platform(String name, boolean proxy) {
		this.name = name;
		this.proxy = proxy;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isProxy() {
		return proxy;
	}
}
