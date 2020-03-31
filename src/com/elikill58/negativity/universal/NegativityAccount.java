package com.elikill58.negativity.universal;

import java.util.UUID;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
public final class NegativityAccount {

	private final UUID playerId;
	private String lang;

	public NegativityAccount(UUID playerId, String lang) {
		this.playerId = playerId;
		this.lang = lang;
	}

	public String getUUID() {
		return this.playerId.toString();
	}

	public String getLang() {
		return lang;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}
