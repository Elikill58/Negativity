package com.elikill58.negativity.universal.ban;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

public class BaseBan {

	private final UUID playerId;
	private final String reason;
	private final String bannedBy;
	private final boolean isDefinitive;
	private final BanType banType;
	private final long expirationTime;
	@Nullable
	private final String cheatName;

	public BaseBan(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		this.playerId = playerId;
		this.reason = reason;
		this.bannedBy = bannedBy;
		this.isDefinitive = isDefinitive;
		this.banType = banType;
		this.expirationTime = expirationTime;
		this.cheatName = cheatName;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public String getReason() {
		return reason;
	}

	public String getBannedBy() {
		return bannedBy;
	}

	public boolean isDefinitive() {
		return isDefinitive;
	}

	public BanType getBanType() {
		return banType;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	@Nullable
	public String getCheatName() {
		return cheatName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BaseBan)) return false;
		BaseBan baseBan = (BaseBan) o;
		return isDefinitive == baseBan.isDefinitive &&
				expirationTime == baseBan.expirationTime &&
				playerId.equals(baseBan.playerId) &&
				reason.equals(baseBan.reason) &&
				bannedBy.equals(baseBan.bannedBy) &&
				banType == baseBan.banType &&
				Objects.equals(cheatName, baseBan.cheatName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
	}
}
