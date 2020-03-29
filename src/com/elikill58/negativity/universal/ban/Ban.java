package com.elikill58.negativity.universal.ban;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

public final class Ban {

	private final UUID playerId;
	private final String reason;
	private final String bannedBy;
	private final BanType banType;
	private final long expirationTime;
	@Nullable
	private final String cheatName;
	private final BanStatus status;

	public Ban(UUID playerId, String reason, String bannedBy, BanType banType, long expirationTime, @Nullable String cheatName, BanStatus status) {
		this.playerId = playerId;
		this.reason = reason;
		this.bannedBy = bannedBy;
		this.banType = banType;
		this.expirationTime = expirationTime;
		this.cheatName = cheatName;
		this.status = status;
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
		return expirationTime <= 0;
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

	public BanStatus getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ban ban = (Ban) o;
		return expirationTime == ban.expirationTime &&
				playerId.equals(ban.playerId) &&
				reason.equals(ban.reason) &&
				bannedBy.equals(ban.bannedBy) &&
				banType == ban.banType &&
				Objects.equals(cheatName, ban.cheatName) &&
				status == ban.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerId, reason, bannedBy, banType, expirationTime, cheatName, status);
	}

	public static Ban from(Ban from, BanStatus status) {
		return new Ban(from.getPlayerId(), from.getReason(), from.getBannedBy(), from.getBanType(), from.getExpirationTime(), from.getCheatName(), status);
	}
}
