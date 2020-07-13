package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanProcessor implements BanProcessor {

	@Nullable
	@Override
	public Ban executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return null;
		}

		long endTime = ban.isDefinitive() ? 0 : ban.getExpirationTime();
		PunishmentType type = ban.isDefinitive() ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Punishment punishment = new Punishment(player.getName(), ban.getPlayerId().toString(), ban.getReason(), ban.getBannedBy(), type, System.currentTimeMillis(), endTime, "", -1);
		// Must be invoked asynchronously because an async event is thrown in there and Bukkit enforces it
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), (Runnable) punishment::create);

		return ban;
	}

	@Nullable
	@Override
	public Ban revokeBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return null;
		}

		// Must be invoked asynchronously because an async event is thrown in there and Bukkit enforces it
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), punishment::delete);
		return loggedBanFrom(playerId, punishment, BanStatus.REVOKED);
	}

	@Override
	public boolean isBanned(UUID playerId) {
		return PunishmentManager.get().isBanned(playerId.toString());
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return null;
		}

		return new Ban(playerId,
				punishment.getReason(),
				punishment.getOperator(),
				BanType.UNKNOW,
				punishment.getEnd(),
				punishment.getReason(),
				BanStatus.ACTIVE,
				punishment.getStart());
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(playerId.toString(), PunishmentType.BAN, false);
		List<Ban> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> loggedBans.add(loggedBanFrom(playerId, punishment, BanStatus.EXPIRED)));
		return loggedBans;
	}

	private Ban loggedBanFrom(UUID playerId, Punishment punishment, BanStatus status) {
		return new Ban(playerId,
				punishment.getReason(),
				punishment.getOperator(),
				BanType.UNKNOW,
				punishment.getEnd(),
				punishment.getReason(),
				status,
				punishment.getStart(),
				punishment.isExpired() ? -1 : System.currentTimeMillis());
	}
}
