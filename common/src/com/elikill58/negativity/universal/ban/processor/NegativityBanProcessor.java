package com.elikill58.negativity.universal.ban.processor;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;

/**
 * This class, additionally to what {@link BaseNegativityBanProcessor} can do, kicks online players that have been banned.
 */
public class NegativityBanProcessor extends BaseNegativityBanProcessor {

	public NegativityBanProcessor(ActiveBanStorage activeBanStorage, @Nullable BanLogsStorage banLogsStorage) {
		super(activeBanStorage, banLogsStorage);
	}
	
	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer nPlayer = NegativityPlayer.getCached(ban.getPlayerId());
		// warn: nPlayer will be null if player is offline

		BanResult executedBan = super.executeBan(ban);

		if (executedBan.getBan() != null && nPlayer != null) {
			BanUtils.kickForBan(nPlayer, executedBan.getBan());
		}

		return executedBan;
	}
}
