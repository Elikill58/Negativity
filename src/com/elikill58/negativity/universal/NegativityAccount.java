package com.elikill58.negativity.universal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
public final class NegativityAccount {

	private final UUID playerId;
	private String lang;
	private final Minerate minerate;
	private int mostClicksPerSecond;
	private final Map<String, Integer> warns;

	public NegativityAccount(UUID playerId) {
		this(playerId, TranslatedMessages.getDefaultLang(), new Minerate(), 0, new HashMap<>());
	}

	public NegativityAccount(UUID playerId, String lang, Minerate minerate, int mostClicksPerSecond, Map<String, Integer> warns) {
		this.playerId = playerId;
		this.lang = lang;
		this.minerate = minerate;
		this.mostClicksPerSecond = mostClicksPerSecond;
		this.warns = warns;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Minerate getMinerate() {
		return minerate;
	}

	public int getMostClicksPerSecond() {
		return mostClicksPerSecond;
	}

	public void setMostClicksPerSecond(int mostClicksPerSecond) {
		this.mostClicksPerSecond = mostClicksPerSecond;
	}

	public int getWarn(String cheatKey) {
		return warns.getOrDefault(cheatKey, 0);
	}

	public int getWarn(Cheat cheat) {
		return getWarn(cheat.getKey());
	}

	public void setWarnCount(Cheat cheat, int count) {
		setWarnCount(cheat.getKey(), count);
	}

	public void setWarnCount(String cheatKey, int count) {
		warns.put(cheatKey, count);
	}

	public Map<String, Integer> getAllWarns() {
		return Collections.unmodifiableMap(warns);
	}
}
