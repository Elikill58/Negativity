package com.elikill58.negativity.universal;

public enum Platform {

	BUNGEE("bungee", "BungeeCord", true),
	FABRIC("fabric", "Fabric", false),
	MINESTOM("minestom", "Minestom", false),
	SPIGOT("spigot", "Spigot", false),
	SPONGE("sponge", "Sponge", false),
	SPONGE8("sponge8", "Sponge8", false),
	SPONGE9("sponge9", "Sponge9", false),
	VELOCITY("velocity", "Velocity", true);
	
	private final String name, completeName;
	private final boolean proxy;
	
	Platform(String name, String completeName, boolean proxy) {
		this.name = name;
		this.completeName = completeName;
		this.proxy = proxy;
	}
	
	public String getCompleteName() {
		return completeName;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isProxy() {
		return proxy;
	}
}
