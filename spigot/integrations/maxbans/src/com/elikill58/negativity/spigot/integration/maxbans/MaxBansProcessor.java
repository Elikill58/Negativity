package com.elikill58.negativity.spigot.integration.maxbans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.HistoryRecord;
import org.maxgamer.maxbans.banmanager.Temporary;

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
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;
import com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider;

public class MaxBansProcessor implements BanProcessor, WarnProcessor {
	
	@Override
	public BanResult executeBan(Ban ban) {
		String playerName = Adapter.getAdapter().getOfflinePlayer(ban.getPlayerId()).getName();

		if (ban.isDefinitive()) {
			MaxBans.instance.getBanManager().ban(playerName, ban.getReason(), ban.getBannedBy());
		} else {
			MaxBans.instance.getBanManager().tempban(playerName, ban.getReason(), ban.getBannedBy(), ban.getExpirationTime());
		}
		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		String playerName = Adapter.getAdapter().getOfflinePlayer(playerId).getName();
		org.maxgamer.maxbans.banmanager.Ban revokedBan = MaxBans.instance.getBanManager().getBan(playerName);
		if (revokedBan == null) {
			return null;
		}
		MaxBans.instance.getBanManager().unban(playerName);

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
		org.maxgamer.maxbans.banmanager.Ban ban = MaxBans.instance.getBanManager().getBan(Adapter.getAdapter().getOfflinePlayer(playerId).getName());

		long expirationTime = -1;
		if (ban instanceof Temporary) {
			expirationTime = ((Temporary) ban).getExpires();
		}

		return new Ban(playerId, ban.getReason(), ban.getBanner(), SanctionnerType.UNKNOW, expirationTime, ban.getReason(), null, BanStatus.ACTIVE, ban.getCreated());
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Ban> loggedBans = new ArrayList<>();
		for (HistoryRecord record : MaxBans.instance.getBanManager().getHistory(Adapter.getAdapter().getOfflinePlayer(playerId).getName())) {
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
	public WarnResult executeWarn(Warn warn) {
		MaxBans.instance.getBanManager().warn(Adapter.getAdapter().getOfflinePlayer(warn.getPlayerId()).getName(), warn.getReason(), warn.getWarnedByName());
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(UUID playerId, String revoker) {
		MaxBans.instance.getBanManager().clearWarnings(Adapter.getAdapter().getOfflinePlayer(playerId).getName());
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(Warn warn, String revoker) {
		String playerName = Adapter.getAdapter().getOfflinePlayer(warn.getPlayerId()).getName();
		for(org.maxgamer.maxbans.banmanager.Warn w : MaxBans.instance.getBanManager().getWarnings(playerName)) {
			if(w.getReason().equals(warn.getReason()) && w.getBanner().equals(warn.getWarnedBy())) { // only way found to get warn
				MaxBans.instance.getBanManager().deleteWarning(playerName, w);
				return new WarnResult(WarnResultType.DONE);
			}
		}
		return new WarnResult(WarnResultType.NOT_WARNED);
	}

	@Override
	public List<Warn> getWarn(UUID playerId) {
		return MaxBans.instance.getBanManager().getWarnings(Adapter.getAdapter().getOfflinePlayer(playerId).getName()).stream().map(w -> toWarn(playerId, w)).collect(Collectors.toList());
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		List<Warn> list = new ArrayList<>();
		MaxBans.instance.getBanManager().getUsers(ip).forEach((playerName) -> {
			UUID uuid = Adapter.getAdapter().getOfflinePlayer(playerName).getUniqueId();
			for(org.maxgamer.maxbans.banmanager.Warn w : MaxBans.instance.getBanManager().getWarnings(playerName)) {
				list.add(toWarn(uuid, w));
			}
		});
		return list;
	}
	
	private Warn toWarn(UUID uuid, org.maxgamer.maxbans.banmanager.Warn w) {
		return new Warn(uuid, w.getReason(), w.getBanner(), SanctionnerType.MOD, null, 0);
	}
	
	@Override
	public String getName() {
		return "MaxBans";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from MaxBans plugin");
	}
	
	public static class Provider implements BanProcessorProvider, WarnProcessorProvider, PluginDependentExtension {
		
		@Override
		public String getId() {
			return "maxbans";
		}
		
		@Override
		public MaxBansProcessor create(Adapter adapter) {
			return new MaxBansProcessor();
		}
		
		@Override
		public String getPluginId() {
			return "MaxBans";
		}
	}
}
