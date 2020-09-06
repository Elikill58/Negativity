package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.HistoryRecord;
import org.maxgamer.maxbans.banmanager.Temporary;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

public class MaxBansProcessor implements BanProcessor {
	
	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}

		if (ban.isDefinitive()) {
			MaxBans.instance.getBanManager().ban(player.getName(), ban.getReason(), ban.getBannedBy());
		} else {
			MaxBans.instance.getBanManager().tempban(player.getName(), ban.getReason(), ban.getBannedBy(), ban.getExpirationTime());
		}
		return new BanResult(BanResultType.DONE, ban);
	}

	@Nullable
	@Override
	public Ban revokeBan(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return null;
		}

		org.maxgamer.maxbans.banmanager.Ban revokedBan = MaxBans.instance.getBanManager().getBan(player.getName());
		if (revokedBan == null) {
			return null;
		}
		MaxBans.instance.getBanManager().unban(player.getName());

		long expirationTime = -1;
		if (revokedBan instanceof Temporary) {
			expirationTime = ((Temporary) revokedBan).getExpires();
		}
		long revocationTime = System.currentTimeMillis();
		return new Ban(playerId, revokedBan.getReason(), revokedBan.getBanner(), BanType.UNKNOW, expirationTime, revokedBan.getReason(), BanStatus.REVOKED, revokedBan.getCreated(), revocationTime);
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return null;
		}

		org.maxgamer.maxbans.banmanager.Ban ban = MaxBans.instance.getBanManager().getBan(player.getName());

		long expirationTime = -1;
		if (ban instanceof Temporary) {
			expirationTime = ((Temporary) ban).getExpires();
		}

		return new Ban(playerId, ban.getReason(), ban.getBanner(), BanType.UNKNOW, expirationTime, ban.getReason(), BanStatus.ACTIVE, ban.getCreated());
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return Collections.emptyList();
		}

		List<Ban> loggedBans = new ArrayList<>();
		for (HistoryRecord record : MaxBans.instance.getBanManager().getHistory(player.getName())) {
			loggedBans.add(new Ban(playerId, record.getMessage(), record.getBanner(), BanType.UNKNOW, 0, record.getMessage(), BanStatus.EXPIRED, record.getCreated()));
		}
		return loggedBans;
	}
}
