package com.elikill58.negativity.universal.ban.processor;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;
import com.elikill58.negativity.universal.permissions.Perm;

/**
 * This class, additionally to what {@link BaseNegativityBanProcessor} can do, kicks online players that have been banned.
 */
public class NegativityBanProcessor extends BaseNegativityBanProcessor {

	public NegativityBanProcessor(ActiveBanStorage activeBanStorage, @Nullable BanLogsStorage banLogsStorage) {
		super(activeBanStorage, banLogsStorage);
	}

	@Nullable
	@Override
	public ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		NegativityPlayer nPlayer = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (nPlayer != null && Perm.hasPerm(nPlayer, "notBanned"))
			return null;

		ActiveBan ban = super.banPlayer(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);

		if (ban != null && nPlayer != null) {
			nPlayer.banEffect();
			String formattedExpTime = new Timestamp(ban.getExpirationTime()).toString().split("\\.", 2)[0];
			nPlayer.kickPlayer(ban.getReason(), formattedExpTime, ban.getBannedBy(), ban.isDefinitive());
		}

		return ban;
	}
}
