package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.Ban;
import org.maxgamer.maxbans.banmanager.HistoryRecord;
import org.maxgamer.maxbans.banmanager.Temporary;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

public class MaxBansProcessor implements BanProcessor {

	@Nullable
	@Override
	public ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (player == null) {
			return null;
		}

		if (isDefinitive) {
			MaxBans.instance.getBanManager().ban(player.getName(), reason, bannedBy);
		} else {
			MaxBans.instance.getBanManager().tempban(player.getName(), reason, bannedBy, expirationTime);
		}
		return new ActiveBan(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (player == null) {
			return null;
		}

		Ban revokedBan = MaxBans.instance.getBanManager().getBan(player.getName());
		if (revokedBan == null) {
			return null;
		}
		MaxBans.instance.getBanManager().unban(player.getName());

		boolean isDefinitive = false;
		long expirationTime = 0;
		if (revokedBan instanceof Temporary) {
			expirationTime = ((Temporary) revokedBan).getExpires();
		} else {
			isDefinitive = true;
		}
		return new LoggedBan(playerId, revokedBan.getReason(), revokedBan.getBanner(), isDefinitive, BanType.UNKNOW, expirationTime, revokedBan.getReason(), true);
	}

	@Nullable
	@Override
	public ActiveBan getActiveBan(UUID playerId) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (player == null) {
			return null;
		}

		Ban ban = MaxBans.instance.getBanManager().getBan(player.getName());

		boolean isDefinitive = false;
		long expirationTime = 0;
		if (ban instanceof Temporary) {
			expirationTime = ((Temporary) ban).getExpires();
		} else {
			isDefinitive = true;
		}

		return new ActiveBan(playerId, ban.getReason(), ban.getBanner(), isDefinitive, BanType.UNKNOW, expirationTime, ban.getReason());
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (player == null) {
			return Collections.emptyList();
		}

		List<LoggedBan> loggedBans = new ArrayList<>();
		for (HistoryRecord record : MaxBans.instance.getBanManager().getHistory(player.getName())) {
			loggedBans.add(new LoggedBan(playerId, record.getMessage(), record.getBanner(), false, BanType.UNKNOW, 0, record.getMessage(), false));
		}
		return loggedBans;
	}
}
