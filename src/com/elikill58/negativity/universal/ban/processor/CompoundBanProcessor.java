package com.elikill58.negativity.universal.ban.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;

public class CompoundBanProcessor implements BanProcessor {

	private final List<BanProcessor> processors;

	public CompoundBanProcessor(List<BanProcessor> processors) {
		this.processors = processors;
	}

	@Nullable
	@Override
	public ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		ActiveBan executedBan = null;
		for (BanProcessor processor : processors) {
			ActiveBan executed = processor.banPlayer(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
			if (executed != null) {
				executedBan = executed;
			}
		}
		return executedBan;
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		LoggedBan revokedBan = null;
		for (BanProcessor processor : processors) {
			LoggedBan revoked = processor.revokeBan(playerId);
			if (revoked != null) {
				revokedBan = revoked;
			}
		}
		return revokedBan;
	}

	@Override
	public boolean isBanned(UUID playerId) {
		for (BanProcessor processor : processors) {
			if (processor.isBanned(playerId)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	@Override
	public ActiveBan getActiveBan(UUID playerId) {
		for (BanProcessor processor : processors) {
			ActiveBan activeBan = processor.getActiveBan(playerId);
			if (activeBan != null) {
				return activeBan;
			}
		}
		return null;
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		List<LoggedBan> loggedBans = new ArrayList<>();
		for (BanProcessor processor : processors) {
			loggedBans.addAll(processor.getLoggedBans(playerId));
		}
		return loggedBans;
	}
}
