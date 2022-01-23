package com.elikill58.negativity.universal.keys;

import com.elikill58.negativity.universal.Version;

public enum SpecialKeys implements IDetectionKeys<SpecialKeys> {

	INVALID_NAME("INVALID-NAME"),
	MAX_PLAYER_PER_IP("MAX-PLAYER-BY-IP"),
	MC_LEAKS("MCLEAKS"),
	SERVER_CRASHER("SERVER-CRASHER"),
	WORLD_DOWNLOADER("WORLD-DOWNLOADER");
	

	private final String key;
	private final Version minVersion;
	
	private SpecialKeys(String key) {
		this(key, Version.V1_7);
	}
	
	private SpecialKeys(String key, Version minVersion) {
		this.key = key;
		this.minVersion = minVersion;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Version getMinVersion() {
		return minVersion;
	}
	
	@Override
	public String toString() {
		return getLowerKey();
	}
}
