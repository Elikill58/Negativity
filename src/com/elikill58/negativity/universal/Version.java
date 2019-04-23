package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.adapter.Adapter;

public enum Version {
	
	V1_7(7, 500), V1_8(8, 500), V1_9(9, 400), V1_10(10, 400), V1_11(11, 300), V1_12(12, 150), V1_13(13, 150), V1_14(14, 100), V1_15(15, 100), V1_16(16, 100), HIGHER(42, 100);

	private final int power, timeBetweenRegen;

	Version(int power, int timeBetweenRegen) {
		this.power = power;
		this.timeBetweenRegen = timeBetweenRegen;
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

	public static boolean isNewer(Version v1, Version v2) {
		return v1.isNewerThan(v2);
	}

	public static boolean isNewerOrEquals(Version v1, Version v2) {
		return v1.isNewerOrEquals(v2);
	}

	public static Version getVersion(String version) {
		for (Version v : Version.values())
			if (version.toLowerCase().startsWith(v.name().toLowerCase()))
				return v;
		return HIGHER;
	}
	
	public static Version getVersion() {
		for (Version v : Version.values())
			if (Adapter.getAdapter().getVersion().toLowerCase().startsWith(v.name().toLowerCase()))
				return v;
		return HIGHER;
	}

	public int getTimeBetweenTwoRegenFromVersion(){
		return timeBetweenRegen;
	}
}
