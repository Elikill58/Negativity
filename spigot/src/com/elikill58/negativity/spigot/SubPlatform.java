package com.elikill58.negativity.spigot;

import java.util.concurrent.Callable;

public enum SubPlatform {

	CRAFTBUKKIT("CraftBukkit", () -> {
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			return false;
		} catch (ClassNotFoundException e) {
			return true;
		}
	}),
	SPIGOT("Spigot", () -> false),
	FOLIA("Folia", () -> {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {}
		return false;
	}),
	PAPER("Paper", () -> {
		try {
			Class.forName("com.destroystokyo.paper.PaperVersionFetcher");
			return true;
		} catch (ClassNotFoundException e) {}
		return false;
	}),
	MOHIST("Mohist", () -> {
		try {
			Class.forName("com.mohistmc.MohistMC");
			return true;
		} catch (ClassNotFoundException e) {}
		return false;
	});
	
	private final String name;
	private final Callable<Boolean> isThis;
	
	private SubPlatform(String name, Callable<Boolean> isThis) {
		this.name = name;
		this.isThis = isThis;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isThis() {
		try {
			return isThis != null && isThis.call();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static SubPlatform getSubPlatform() {
		for(SubPlatform sub : values())
			if(sub.isThis())
				return sub;
		return SPIGOT; // default platform
	}
}
