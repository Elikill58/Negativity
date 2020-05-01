package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;

public enum Version {
	
	V1_7(7, 500, 0, 5),
	V1_8(8, 500, 6, 47),
	V1_9(9, 400, 48, 110),
	V1_10(10, 400, 201, 210),
	V1_11(11, 300, 301, 316),
	V1_12(12, 150, 317, 340),
	V1_13(13, 150, 341, 404),
	V1_14(14, 100, 441, 500),
	V1_15(15, 100, 550, 578),
	V1_16(16, 100, 700, 720),
	V1_17(16, 100, 800, 1000),
	HIGHER(42, 100, 1000, 1000);

	private final int power, timeBetweenRegen;
	private final List<Integer> protocolNumber = new ArrayList<>();

	Version(int power, int timeBetweenRegen, int firstProtocolNumber, int lastProtocolNumber) {
		this.power = power;
		this.timeBetweenRegen = timeBetweenRegen;
		for(int i = firstProtocolNumber; i <= lastProtocolNumber; i++)
			this.protocolNumber.add(i);
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
	
	public static Version getVersionByProtocolID(int id) {
		for (Version v : Version.values())
			if (v.getProtocolNumber().contains(id))
				return v;
		return HIGHER;
	}
	
	public static boolean isNewerOrEquals(Version v1, Version v2) {
		return v1.isNewerOrEquals(v2);
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
