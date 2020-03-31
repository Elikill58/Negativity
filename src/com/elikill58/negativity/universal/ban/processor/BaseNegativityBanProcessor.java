package com.elikill58.negativity.universal.ban.processor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;

/**
 * This ban processor simply saves bans (active and logged) in a configurable storage ({@link ActiveBanStorage} and {@link BanLogsStorage} respectively).
 * <p>
 * It is important to know its sole purpose is to manage bans, and will not do anything on the game server,
 * like kicking the player when {@link BanProcessor#executeBan(Ban) executing a ban}.
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
	public Ban executeBan(Ban ban) {
		if (isBanned(ban.getPlayerId())) {
			return null;
		}

		activeBanStorage.save(ban);
		return ban;
	}

	@Nullable
	@Override
	public Ban revokeBan(UUID playerId) {
		Ban activeBan = activeBanStorage.load(playerId);
		if (activeBan == null)
			return null;

		activeBanStorage.remove(playerId);
		Ban revokedLoggedBan = Ban.from(activeBan, BanStatus.REVOKED);

		if (banLogsStorage != null) {
			banLogsStorage.save(revokedLoggedBan);
		}

		return revokedLoggedBan;
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		Ban activeBan = activeBanStorage.load(playerId);
		if (activeBan == null) {
			return null;
		}

		long now = System.currentTimeMillis();
		if (activeBan.isDefinitive() || activeBan.getExpirationTime() > now) {
			return activeBan;
		}

		activeBanStorage.remove(playerId);
		if (banLogsStorage != null) {
			banLogsStorage.save(Ban.from(activeBan, BanStatus.EXPIRED));
		}

		return null;
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		if (banLogsStorage == null) {
			return Collections.emptyList();
		}
		return banLogsStorage.load(playerId);
	}
}
