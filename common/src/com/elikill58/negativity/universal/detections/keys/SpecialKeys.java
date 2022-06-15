package com.elikill58.negativity.universal.detections.keys;

import java.nio.file.Path;

import com.elikill58.negativity.universal.Version;

public enum SpecialKeys implements IDetectionKey<SpecialKeys> {

	BANNED_NAME("BANNED-NAME"),
	INVALID_NAME("INVALID-NAME"),
	MAX_PLAYER_PER_IP("MAX-PLAYER-BY-IP"),
	MC_LEAKS("MCLEAKS"),
	SERVER_CRASHER("SERVER-CRASHER"),
	WORLD_DOWNLOADER("WORLD-DOWNLOADER");
	

	public static final String BUNDLED_SPECIAL_MODULES_BASE = CheatKeys.BUNDLED_MODULES_BASE + "special/";
	public static final Path MODULE_FOLDER = CheatKeys.MODULE_FOLDER.resolve("special");
	
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

	@Override
	public Path getFolder() {
		return MODULE_FOLDER;
	}

	@Override
	public String getPathBundle() {
		return BUNDLED_SPECIAL_MODULES_BASE;
	}
}
