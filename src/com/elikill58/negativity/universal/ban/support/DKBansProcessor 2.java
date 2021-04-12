package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.entry.HistoryEntry;
import ch.dkrieger.bansystem.lib.player.history.entry.Unban;

public class DKBansProcessor implements BanProcessor {

	@Override
	public Ban executeBan(Ban ban) {
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(ban.getPlayerId());
		if (player.ban(BanType.NETWORK, ban.isDefinitive() ? -1 : ban.getRevocationTime() - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS, ban.getReason(), -1, ban.getBannedBy()) != null)
			return ban;
		return null;
	}

	@Override
	public Ban revokeBan(UUID playerId) {
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(playerId);
		Unban unban = player.unban(BanType.NETWORK);
		return parseToNegativityBan(unban, BanStatus.REVOKED);
	}

	@Override
	public Ban getActiveBan(UUID playerId) {
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(playerId);
		ch.dkrieger.bansystem.lib.player.history.entry.Ban ban = player.getHistory().getBan(BanType.NETWORK);
		return ban == null ? null : parseToNegativityBan(ban, BanStatus.ACTIVE);
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Ban> list = new ArrayList<Ban>();
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(playerId);
		player.getHistory().getBans(BanType.NETWORK).forEach((b) -> {
			list.add(parseToNegativityBan(b, b.getRemaining() <= 0 ? BanStatus.EXPIRED : BanStatus.ACTIVE));
		});
		return list;
	}

	private Ban parseToNegativityBan(HistoryEntry he, BanStatus bs) {
		return new Ban(he.getUUID(), he.getReason(), he.getStaffName(),
				he.getStaffAsPlayer() == null ? com.elikill58.negativity.universal.ban.BanType.MOD
						: com.elikill58.negativity.universal.ban.BanType.CONSOLE,
				he.getTimeStamp(), null, bs);
	}
}
