package com.elikill58.negativity.universal.ban.processor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;

/**
 * This ban processor simply saves bans (active and logged) in a configurable storage ({@link ActiveBanStorage} and {@link BanLogsStorage} respectively).
 * <p>
 * It is important to know its sole purpose is to manage bans, and will not do anything on the game server,
 * like kicking the player when {@link #banPlayer(UUID, String, String, boolean, BanType, long, String) executing a ban}.
 * If you want direct actions on the game server use {@link NegativityBanProcessor} instead.
 */
public class BaseNegativityBanProcessor implements BanProcessor {

	protected final ActiveBanStorage activeBanStorage;
	@Nullable
	protected final BanLogsStorage banLogsStorage;

	public BaseNegativityBanProcessor(ActiveBanStorage activeBanStorage, @Nullable BanLogsStorage banLogsStorage) {
		this.activeBanStorage = activeBanStorage;
		this.banLogsStorage = banLogsStorage;
	}

	@Nullable
	@Override
	public ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		if (isBanned(playerId)) {
			return null;
		}

		ActiveBan ban = new ActiveBan(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
		activeBanStorage.save(ban);
		return ban;
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		ActiveBan activeBan = activeBanStorage.load(playerId);
		if (activeBan == null)
			return null;

		activeBanStorage.remove(playerId);
		LoggedBan revokedLoggedBan = LoggedBan.from(activeBan, true);

		if (banLogsStorage != null) {
			banLogsStorage.save(revokedLoggedBan);
		}

		return revokedLoggedBan;
	}

	@Nullable
	@Override
	public ActiveBan getActiveBan(UUID playerId) {
		ActiveBan activeBan = activeBanStorage.load(playerId);
		if (activeBan == null) {
			return null;
		}

		long now = System.currentTimeMillis();
		if (activeBan.isDefinitive() || activeBan.getExpirationTime() > now) {
			return activeBan;
		}

		activeBanStorage.remove(playerId);
		if (banLogsStorage != null) {
			banLogsStorage.save(LoggedBan.from(activeBan, false));
		}

		return null;
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		if (banLogsStorage == null) {
			return Collections.emptyList();
		}
		return banLogsStorage.load(playerId);
	}
}
