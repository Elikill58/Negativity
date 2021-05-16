package com.elikill58.negativity.spigot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.PlatformDependentExtension;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

public class BukkitBanProcessor implements BanProcessor {
	
	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}

		Date expirationDate = ban.isDefinitive() ? null : Date.from(Instant.ofEpochMilli(ban.getExpirationTime()));
		BanEntry banEntry = Bukkit.getServer().getBanList(BanList.Type.NAME)
				.addBan(player.getName(), ban.getReason(), expirationDate, ban.getBannedBy());
		if (banEntry == null) {
			Adapter.getAdapter().debug("[BukkitProcessor] Cannot find BanEntry " + ban.getPlayerId());
			return null;
		}
		BanUtils.kickForBan(player, ban);

		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
		BanEntry banEntry = banList.getBanEntry(playerId.toString());
		if (banEntry == null) {
			return new BanResult(BanResultType.ALREADY_UNBANNED);
		}

		banList.pardon(playerId.toString());
		return new BanResult(loggedBanFrom(banEntry, playerId, true));
	}

	@Override
	public boolean isBanned(UUID playerId) {
		return Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(playerId.toString());
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		BanEntry banEntry = Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(playerId.toString());
		if (banEntry == null) {
			return null;
		}

		long expirationTime = -1;
		Date expiration = banEntry.getExpiration();
		if (expiration != null) {
			expirationTime = expiration.getTime();
		}

		String reason = banEntry.getReason();
		if (reason == null) {
			reason = "";
		}

		long executionTime = banEntry.getCreated().getTime();
		return new Ban(playerId, reason, banEntry.getSource(), BanType.UNKNOW, expirationTime, reason, null, BanStatus.ACTIVE, executionTime);
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Ban> loggedBans = new ArrayList<>();
		Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntries()
				.forEach(entry -> loggedBans.add(loggedBanFrom(entry, playerId, false)));
		return loggedBans;
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return Collections.emptyList();
	}

	private Ban loggedBanFrom(BanEntry banEntry, UUID playerId, boolean revoked) {
		long expirationTime = -1;
		Date expiration = banEntry.getExpiration();
		if (expiration != null) {
			expirationTime = expiration.getTime();
		}

		String reason = banEntry.getReason();
		if (reason == null) {
			reason = "";
		}

		BanStatus status = revoked ? BanStatus.REVOKED : BanStatus.EXPIRED;
		long executionTime = banEntry.getCreated().getTime();
		long revocationTime = revoked ? System.currentTimeMillis() : -1;
		return new Ban(playerId, reason, banEntry.getSource(), BanType.UNKNOW, expirationTime, reason, null, status, executionTime, revocationTime);
	}
	
	public static class Provider implements BanProcessorProvider, PlatformDependentExtension {
		
		@Override
		public String getId() {
			return "bukkit";
		}
		
		@Nullable
		@Override
		public BanProcessor create(Adapter adapter) {
			return new BukkitBanProcessor();
		}
		
		@Override
		public Platform getPlatform() {
			return Platform.SPIGOT;
		}
	}
}
