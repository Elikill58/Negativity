package com.elikill58.negativity.universal.ban.support;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

public class BukkitBanProcessor implements BanProcessor {

	@Nullable
	@Override
	public Ban executeBan(Ban ban) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(ban.getPlayerId());
		if (player == null) {
			return null;
		}

		Date expirationDate = ban.isDefinitive() ? null : Date.from(Instant.ofEpochMilli(ban.getExpirationTime()));
		BanEntry banEntry = Bukkit.getServer().getBanList(BanList.Type.NAME)
				.addBan(ban.getPlayerId().toString(), ban.getReason(), expirationDate, ban.getBannedBy());
		if (banEntry == null) {
			return null;
		}

		BanUtils.kickForBan(player, ban);

		return ban;
	}

	@Nullable
	@Override
	public Ban revokeBan(UUID playerId) {
		BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
		BanEntry banEntry = banList.getBanEntry(playerId.toString());
		if (banEntry == null) {
			return null;
		}

		banList.pardon(playerId.toString());
		return loggedBanFrom(banEntry, playerId, true);
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
		return new Ban(playerId, reason, banEntry.getSource(), BanType.UNKNOW, expirationTime, reason, BanStatus.ACTIVE, executionTime);
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Ban> loggedBans = new ArrayList<>();
		Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntries()
				.forEach(entry -> loggedBans.add(loggedBanFrom(entry, playerId, false)));
		return loggedBans;
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
		return new Ban(playerId, reason, banEntry.getSource(), BanType.UNKNOW, expirationTime, reason, status, executionTime, revocationTime);
	}
}
