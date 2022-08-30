package com.elikill58.negativity.universal.warn;

import java.util.Objects;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public final class Warn {

	private final int id;
	private final UUID playerId;
	private final String reason, warnedBy, ip, revocationBy;
	private final SanctionnerType banType;
	private final long executionTime, revocationTime;
	private final boolean active;

	public Warn(UUID playerId, String reason, String warnedByName, SanctionnerType banType, @Nullable String ip,
			long executionTime) {
		this(0, playerId, reason, warnedByName, banType, ip, executionTime, true, -1, null);
	}

	public Warn(int id, UUID playerId, String reason, String warnedByName, SanctionnerType banType, @Nullable String ip,
			long executionTime, boolean active, long revocationTime, String revocationBy) {
		this.id = id;
		this.playerId = playerId;
		this.reason = reason;
		this.warnedBy = warnedByName;
		this.banType = banType;
		this.ip = ip;
		this.executionTime = executionTime;
		this.revocationTime = revocationTime;
		this.active = active;
		this.revocationBy = revocationBy;
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

	public String getWarnedBy() {
		return warnedBy;
	}

	/**
	 * Get UUID of staff that warned
	 * 
	 * @return the uuid
	 */
	public UUID getWarnedByUUID() {
		return UniversalUtils.isUUID(warnedBy) ? UUID.fromString(warnedBy) : null;
	}

	public String getWarnedByName() {
		if(UniversalUtils.isUUID(warnedBy))
			return Adapter.getAdapter().getOfflinePlayer(UUID.fromString(warnedBy)).getName();
		return warnedBy;
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

	@Nullable
	public String getRevocationBy() {
		return revocationBy;
	}

	@Nullable
	public String getRevocationByName() {
		if(revocationBy == null)
			return null;
		if(UniversalUtils.isUUID(revocationBy))
			return Adapter.getAdapter().getOfflinePlayer(UUID.fromString(revocationBy)).getName();
		return revocationBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Warn warn = (Warn) o;
		return playerId.equals(warn.playerId) && reason.equals(warn.reason) && warnedBy.equals(warn.warnedBy)
				&& banType == warn.banType && executionTime == warn.executionTime
				&& revocationTime == warn.revocationTime && active == warn.active && revocationBy == warn.revocationBy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerId, reason, warnedBy, banType, executionTime, revocationTime, active);
	}

	public static Warn from(Warn from) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getWarnedBy(),
				from.getSanctionnerType(), from.getIp(), from.getExecutionTime(), from.isActive(),
				from.getRevocationTime(), from.getRevocationBy());
	}

	public static Warn activeFrom(Warn from) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getWarnedBy(),
				from.getSanctionnerType(), from.getIp(), from.getExecutionTime(), true, -1, null);
	}

	public static Warn revokedFrom(Warn from, long revocationTime, String revoker) {
		return new Warn(from.getId(), from.getPlayerId(), from.getReason(), from.getWarnedBy(),
				from.getSanctionnerType(), from.getIp(), from.getExecutionTime(), false, revocationTime, revoker);
	}

	public static Warn active(UUID playerId, String reason, String bannedBy, SanctionnerType banType,
			long expirationTime, String ip) {
		return new Warn(0, playerId, reason, bannedBy, banType, ip, expirationTime, true, -1, null);
	}
}
