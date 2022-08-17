package com.elikill58.negativity.spigot.integration.maxbans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.HistoryRecord;
import org.maxgamer.maxbans.banmanager.Temporary;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

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

	@Override
	public BanResult revokeBan(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER);
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
		return new BanResult(new Ban(playerId, revokedBan.getReason(), revokedBan.getBanner(), SanctionnerType.UNKNOW, expirationTime, revokedBan.getReason(), null, BanStatus.REVOKED, revokedBan.getCreated(), revocationTime));
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

		return new Ban(playerId, ban.getReason(), ban.getBanner(), SanctionnerType.UNKNOW, expirationTime, ban.getReason(), null, BanStatus.ACTIVE, ban.getCreated());
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return Collections.emptyList();
		}

		List<Ban> loggedBans = new ArrayList<>();
		for (HistoryRecord record : MaxBans.instance.getBanManager().getHistory(player.getName())) {
			loggedBans.add(new Ban(playerId, record.getMessage(), record.getBanner(), SanctionnerType.UNKNOW, 0, record.getMessage(), null, BanStatus.EXPIRED, record.getCreated()));
		}
		return loggedBans;
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		List<Ban> list = new ArrayList<>();
		MaxBans.instance.getBanManager().getUsers(ip).forEach((playerName) -> {
			org.maxgamer.maxbans.banmanager.Ban ban = MaxBans.instance.getBanManager().getBan(playerName);
			long expirationTime = -1;
			if (ban instanceof Temporary) {
				expirationTime = ((Temporary) ban).getExpires();
			}
			list.add(new Ban(UUID.fromString(ban.getId()), ban.getReason(), ban.getBanner(), SanctionnerType.UNKNOW, expirationTime, ban.getReason(), null, BanStatus.ACTIVE, ban.getCreated()));
		});
		return list;
	}
	
	@Override
	public List<Ban> getAllBans() {
		List<Ban> loggedBans = new ArrayList<>();
		MaxBans.instance.getBanManager().getBans().forEach((name, ban) -> {
			loggedBans.add(new Ban(UUID.fromString(ban.getId()), ban.getKickMessage(), ban.getBanner(), SanctionnerType.UNKNOW, 0, ban.getKickMessage(), null, BanStatus.ACTIVE, ban.getCreated()));
		});
		return loggedBans;
	}
	
	@Override
	public String getName() {
		return "MaxBans";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from MaxBans plugin");
	}
	
	public static class Provider implements BanProcessorProvider, PluginDependentExtension {
		
		@Override
		public String getId() {
			return "maxbans";
		}
		
		@Override
		public BanProcessor create(Adapter adapter) {
			return new MaxBansProcessor();
		}
		
		@Override
		public String getPluginId() {
			return "MaxBans";
		}
	}
}
