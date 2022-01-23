package com.elikill58.negativity.universal.keys;

import com.elikill58.negativity.universal.Version;

public enum CheatKeys implements IDetectionKeys<CheatKeys> {

	ALL("ALL"),
	AIM_BOT("AIMBOT"),
	AIR_JUMP("AIRJUMP"),
	AIR_PLACE("AIRPLACE"),
	ANTI_KNOCKBACK("ANTIKNOCKBACK"),
	ANTI_POTION("ANTIPOTION"),
	AUTO_CLICK("AUTOCLICK"),
	AUTO_STEAL("AUTOSTEAL"),
	BLINK("BLINK"),
	CHAT("CHAT"),
	CRITICAL("CRITICAL"),
	ELYTRA_FLY("ELYTRAFLY", Version.V1_9),
	FAST_BOW("FASTBOW"),
	FAST_EAT("FASTEAT"),
	FAST_LADDER("FASTLADDER"),
	FAST_PLACE("FASTPLACE"),
	FAST_STAIRS("FASTSTAIRS"),
	FLY("FLY"),
	FORCEFIELD("FORCEFIELD"),
    GROUND_SPOOF("GROUNDSPOOF"),
	INVENTORY_MOVE("INVENTORYMOVE"),
	JESUS("JESUS"),
	NO_FALL("NOFALL"),
	NO_PITCH_LIMIT("NOPITCHLIMIT"),
	NO_SLOW_DOWN("NOSLOWDOWN"),
	NO_WEB("NOWEB"),
	NUKER("NUKER"),
	PINGSPOOF("PINGSPOOF"),
	PHASE("PHASE"),
	REACH("REACH"),
	REGEN("REGEN"),
	SCAFFOLD("SCAFFOLD"),
	SNEAK("SNEAK"),
	SPEED("SPEED"),
	SPIDER("SPIDER"),
	STEP("STEP"),
	SUPER_KNOCKBACK("SUPERKNOCKBACK"),
	TIMER("TIMER"),
	XRAY("XRAY");
	
	private final String key;
	private final Version minVersion;
	
	private CheatKeys(String key) {
		this(key, Version.V1_7);
	}
	
	private CheatKeys(String key, Version minVersion) {
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
