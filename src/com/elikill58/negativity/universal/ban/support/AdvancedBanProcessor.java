package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanProcessor implements BanProcessor {

	@Nullable
	@Override
	public ActiveBan banPlayer(UUID playerId, String reason, String bannedBy, boolean isDefinitive, BanType banType, long expirationTime, @Nullable String cheatName) {
		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (player == null) {
			return null;
		}

		String name;
		PunishmentType punishmentType;
		if (UniversalUtils.isValidIP(player.getIP())) {
			name = player.getIP();
			punishmentType = isDefinitive ? PunishmentType.IP_BAN : PunishmentType.TEMP_IP_BAN;
		} else {
			name = player.getName();
			punishmentType = isDefinitive ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		}

		long endTime = isDefinitive ? 0 : expirationTime;
		new Punishment(name, playerId.toString(), reason, bannedBy, punishmentType, System.currentTimeMillis(), endTime, "", -1)
				.create();
		return new ActiveBan(playerId, reason, bannedBy, isDefinitive, banType, expirationTime, cheatName);
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		punishment.delete();
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
