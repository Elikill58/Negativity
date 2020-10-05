package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.List;

public enum Version {
	
	V1_7("1.7", 7, 500, 0, 5),
	V1_8("1.8", 8, 500, 6, 47),
	V1_9("1.9", 9, 400, 48, 110),
	V1_10("1.10", 10, 400, 201, 210),
	V1_11("1.11", 11, 300, 301, 316),
	V1_12("1.12", 12, 150, 317, 340),
	V1_13("1.13", 13, 150, 341, 404),
	V1_14("1.14", 14, 100, 441, 500),
	V1_15("1.15", 15, 100, 550, 578),
	V1_16("1.16", 16, 100, 700, 750),
	V1_17("1.17", 17, 100, 800, 1000),
	HIGHER("higher", 42, 100, 1000, 1000);

	private final int power, timeBetweenRegen;
	private final List<Integer> protocolNumber = new ArrayList<>();
	private final String name;
	
	Version(String name, int power, int timeBetweenRegen, int firstProtocolNumber, int lastProtocolNumber) {
		this.name = name;
		this.power = power;
		this.timeBetweenRegen = timeBetweenRegen;
		for(int i = firstProtocolNumber; i <= lastProtocolNumber; i++)
			this.protocolNumber.add(i);
	}
	
	public String getName() {
		return name;
	}

	public boolean isNewerThan(Version other) {
		return power > other.getPower();
	}
	
	public boolean isNewerOrEquals(Version other) {
		return power >= other.getPower();
	}

	public int getPower() {
		return power;
	}

	public int getTimeBetweenTwoRegenFromVersion(){
		return timeBetweenRegen;
	}

	public List<Integer> getProtocolNumber() {
		return protocolNumber;
	}
	
	public static Version getVersionByName(String name) {
		for (Version v : Version.values())
			if (name.startsWith(v.getName()))
				return v;
		return HIGHER;
	}
	
	public static Version getVersionByProtocolID(int id) {
		for (Version v : Version.values())
			if (v.getProtocolNumber().contains(id))
				return v;
		return HIGHER;
	}
	
	public static Version getVersion() {
		return getVersion(Adapter.getAdapter().getVersion());
	}
	
	public static Version getVersion(String version) {
		for (Version v : Version.values())
			if (version.toLowerCase().startsWith(v.name().toLowerCase()))
				return v;
		return HIGHER;
	}
}
