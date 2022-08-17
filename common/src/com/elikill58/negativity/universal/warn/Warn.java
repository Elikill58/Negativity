package com.elikill58.negativity.universal.warn;

import java.util.Objects;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;

public final class Warn {

	private final int id;
	private final UUID playerId;
	private final String reason, bannedBy, ip;
	private final SanctionnerType banType;
	private final long executionTime, revocationTime;
	private final boolean active;

	public Warn(UUID playerId, String reason, String bannedBy, SanctionnerType banType, @Nullable String ip, long executionTime) {
		this(0, playerId, reason, bannedBy, banType, ip, executionTime, true, -1);
	}

	public Warn(int id, UUID playerId, String reason, String bannedBy, SanctionnerType banType,@Nullable  String ip, long executionTime,
			boolean active, long revocationTime) {
		this.id = id;
		this.playerId = playerId;
		this.reason = reason;
		this.bannedBy = bannedBy;
		this.banType = banType;
		this.ip = ip;
		this.executionTime = executionTime;
		this.revocationTime = revocationTime;
		this.active = active;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isSaved() {
		return id > 0;
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
		return Adapter.getAdapter().getUUID(bannedBy);
	}

	public SanctionnerType getSanctionnerType() {
		return banType;
	}
	
	@Nullable
	public String getIp() {
		return ip;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public long getRevocationTime() {
		return revocationTime;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Warn warn = (Warn) o;
		return playerId.equals(warn.playerId) && reason.equals(warn.reason) && bannedBy.equals(warn.bannedBy)
				&& banType == warn.banType && executionTime == warn.executionTime
				&& revocationTime == warn.revocationTime && active == warn.active;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerId, reason, bannedBy, banType, executionTime, revocationTime, active);
	}

	public static Warn from(Warn from) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getBannedBy(), from.getSanctionnerType(), from.getIp(),
				from.getExecutionTime(), from.isActive(), from.getRevocationTime());
	}

	public static Warn activeFrom(Warn from) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getBannedBy(), from.getSanctionnerType(), from.getIp(),
				from.getExecutionTime(), true, -1);
	}

	public static Warn revokedFrom(Warn from, long revocationTime) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getBannedBy(), from.getSanctionnerType(), from.getIp(),
				from.getExecutionTime(), false, revocationTime);
	}

	public static Warn active(UUID playerId, String reason, String bannedBy, SanctionnerType banType,
			long expirationTime, String ip) {
		return new Warn(0, playerId, reason, bannedBy, banType, ip, expirationTime, true, System.currentTimeMillis());
	}
}
