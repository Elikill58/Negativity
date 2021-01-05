package com.elikill58.negativity.integration.dkbans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.entry.HistoryEntry;
import ch.dkrieger.bansystem.lib.player.history.entry.Unban;

public class DKBansProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(ban.getPlayerId());
		if (player.ban(BanType.NETWORK, ban.getRevocationTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS,
				ban.getReason(), -1, ban.getBannedBy()) != null)
			return new BanResult(BanResultType.DONE, ban);
		return new BanResult(BanResultType.ALREADY_BANNED);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		NetworkPlayer player = BanSystem.getInstance().getPlayerManager().getPlayer(playerId);
		Unban unban = player.unban(BanType.NETWORK);
		return unban == null ? new BanResult(BanResultType.ALREADY_UNBANNED)
				: new BanResult(BanResultType.DONE, parseToNegativityBan(unban, BanStatus.REVOKED));
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
		BanSystem.getInstance().getPlayerManager().getPlayer(playerId).getHistory().getBans(BanType.NETWORK).forEach((b) -> {
			list.add(parseToNegativityBan(b, b.getRemaining() <= 0 ? BanStatus.EXPIRED : BanStatus.ACTIVE));
		});
		return list;
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		List<Ban> list = new ArrayList<Ban>();
		BanSystem.getInstance().getPlayerManager().getPlayers(ip)
				.forEach((p) -> list.addAll(getLoggedBans(p.getUUID())));
		return list;
	}

	private Ban parseToNegativityBan(HistoryEntry he, BanStatus bs) {
		return new Ban(he.getUUID(), he.getReason(), he.getStaffName(),
				he.getStaffAsPlayer() == null ? com.elikill58.negativity.universal.ban.BanType.MOD
						: com.elikill58.negativity.universal.ban.BanType.CONSOLE,
				he.getTimeStamp(), null, he.getIp(), bs);
	}

	public static class Provider implements BanProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "dkbans";
		}

		@Override
		public BanProcessor create(Adapter adapter) {
			return new DKBansProcessor();
		}

		@Override
		public String getPluginId() {
			return "DKBans";
		}
	}
}
