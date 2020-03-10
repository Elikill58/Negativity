package com.elikill58.negativity.universal.ban.processor;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanUtils;
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
	public ActiveBan executeBan(ActiveBan ban) {
		NegativityPlayer nPlayer = Adapter.getAdapter().getNegativityPlayer(ban.getPlayerId());
		if (nPlayer != null && Perm.hasPerm(nPlayer, "notBanned"))
			return null;

		ActiveBan executedBan = super.executeBan(ban);

		if (executedBan != null && nPlayer != null) {
			BanUtils.kickForBan(nPlayer, executedBan);
		}

		return executedBan;
	}
}
