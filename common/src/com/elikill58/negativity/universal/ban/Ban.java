package com.elikill58.negativity.universal.ban;

import java.util.Objects;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.universal.Adapter;

public final class Ban {

	private final UUID playerId;
	private final String reason;
	private final String bannedBy;
	private final BanType banType;
	private final long expirationTime;
	@Nullable
	private final String cheatName, ip;
	private final BanStatus status;
	private final long executionTime;
	private final long revocationTime;

	public Ban(UUID playerId,
			   String reason,
			   String bannedBy,
			   BanType banType,
			   long expirationTime,
			   @Nullable String cheatName,
			   @Nullable String ip,
			   BanStatus status) {
		this(playerId, reason, bannedBy, banType, expirationTime, cheatName, ip, status, -1);
	}

	public Ban(UUID playerId,
			   String reason,
			   String bannedBy,
			   BanType banType,
			   long expirationTime,
			   @Nullable String cheatName,
			   @Nullable String ip,
			   BanStatus status,
			   long executionTime) {
		this(playerId, reason, bannedBy, banType, expirationTime, cheatName, ip, status, executionTime, -1);
	}

	public Ban(UUID playerId,
			   String reason,
			   String bannedBy,
			   BanType banType,
			   long expirationTime,
			   @Nullable String cheatName,
			   @Nullable String ip,
			   BanStatus status,
			   long executionTime,
			   long revocationTime) {
		this.playerId = playerId;
		this.reason = reason;
		this.bannedBy = bannedBy;
		this.banType = banType;
		this.expirationTime = expirationTime;
		this.cheatName = cheatName;
		this.ip = ip;
		this.status = status;
		this.executionTime = executionTime;
		this.revocationTime = revocationTime;
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

	public UUID getBannedByUUID() {
		OfflinePlayer p = Adapter.getAdapter().getOfflinePlayer(bannedBy);
		return p != null ? p.getUniqueId() : null;
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
	
	@Nullable 
	public String getIp() {
		return ip;
	}

	public BanStatus getStatus() {
		return status;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public long getRevocationTime() {
		return revocationTime;
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
				status == ban.status &&
				executionTime == ban.executionTime &&
				revocationTime == ban.revocationTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerId, reason, bannedBy, banType, expirationTime, cheatName, status, executionTime, revocationTime);
	}

	public static Ban from(Ban from, BanStatus status) {
		return new Ban(from.getPlayerId(),
				from.getReason(),
				from.getBannedBy(),
				from.getBanType(),
				from.getExpirationTime(),
				from.getCheatName(),
				from.getIp(),
				status,
				from.getExecutionTime(),
				from.getRevocationTime());
	}

	public static Ban activeFrom(Ban from) {
		return new Ban(from.getPlayerId(),
				from.getReason(),
				from.getBannedBy(),
				from.getBanType(),
				from.getExpirationTime(),
				from.getCheatName(),
				from.getIp(),
				BanStatus.ACTIVE,
				from.getExecutionTime(),
				-1);
	}

	public static Ban revokedFrom(Ban from, long revocationTime) {
		return new Ban(from.getPlayerId(),
				from.getReason(),
				from.getBannedBy(),
				from.getBanType(),
				from.getExpirationTime(),
				from.getCheatName(),
				from.getIp(),
				BanStatus.REVOKED,
				from.getExecutionTime(),
				revocationTime);
	}

	public static Ban active(UUID playerId, String reason, String bannedBy, BanType banType, long expirationTime, @Nullable String cheatName, @Nullable String ip) {
		return new Ban(playerId, reason, bannedBy, banType, expirationTime, cheatName, ip, BanStatus.ACTIVE, System.currentTimeMillis());
	}
}
