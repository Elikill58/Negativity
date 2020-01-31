package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanProcessor implements BanProcessor {

	@Nullable
	@Override
	public ActiveBan executeBan(ActiveBan ban) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(ban.getPlayerId());
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
	public LoggedBan revokeBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return null;
		}

		// Must be invoked asynchronously because an async event is thrown in there and Bukkit enforces it
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), punishment::delete);
		return loggedBanFrom(playerId, punishment, true);
	}

	@Override
	public boolean isBanned(UUID playerId) {
		return PunishmentManager.get().isBanned(playerId.toString());
	}

	@Nullable
	@Override
	public ActiveBan getActiveBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return null;
		}

		return new ActiveBan(playerId,
				punishment.getReason(),
				punishment.getOperator(),
				punishment.getEnd() > 0,
				BanType.UNKNOW,
				punishment.getEnd(),
				punishment.getReason());
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(playerId.toString(), PunishmentType.BAN, false);
		List<LoggedBan> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> loggedBans.add(loggedBanFrom(playerId, punishment, false)));
		return loggedBans;
	}

	private LoggedBan loggedBanFrom(UUID playerId, Punishment punishment, boolean revoked) {
		return new LoggedBan(playerId,
				punishment.getReason(),
				punishment.getOperator(),
				punishment.getEnd() > 0,
				BanType.UNKNOW,
				punishment.getEnd(),
				punishment.getReason(),
				revoked);
	}
}
