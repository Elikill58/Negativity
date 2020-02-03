package com.elikill58.negativity.universal.ban;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

public class LoggedBan extends BaseBan {

	private final boolean isRevoked;

	public LoggedBan(UUID playerId, String reason, String bannedBy, BanType banType, long expirationTime, @Nullable String cheatName, boolean isRevoked) {
		super(playerId, reason, bannedBy, banType, expirationTime, cheatName);
		this.isRevoked = isRevoked;
	}

	public boolean isRevoked() {
		return isRevoked;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LoggedBan)) return false;
		if (!super.equals(o)) return false;
		LoggedBan loggedBan = (LoggedBan) o;
		return isRevoked == loggedBan.isRevoked;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), isRevoked);
	}

	public static LoggedBan from(BaseBan from, boolean isRevoked) {
		return new LoggedBan(from.getPlayerId(), from.getReason(), from.getBannedBy(), from.getBanType(), from.getExpirationTime(), from.getCheatName(), isRevoked);
	}
}
