package com.elikill58.negativity.integration.advancedban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}

		long endTime = ban.isDefinitive() ? 0 : ban.getExpirationTime();
		PunishmentType type = ban.isDefinitive() ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Punishment punishment = new Punishment(player.getName(), ban.getPlayerId().toString(), ban.getReason(), ban.getBannedBy(), type, System.currentTimeMillis(), endTime, "", -1);
		// Must be invoked asynchronously because an async event is thrown in there and Bukkit enforces it
		CompletableFuture.runAsync(punishment::create);

		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return new BanResult(BanResultType.ALREADY_UNBANNED);
		}

		// Must be invoked asynchronously because an async event is thrown in there and Bukkit enforces it
		CompletableFuture.runAsync(punishment::delete);
		
		return new BanResult(loggedBanFrom(playerId, punishment, BanStatus.REVOKED));
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
				null,
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
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return Collections.emptyList();
	}

	private Ban loggedBanFrom(UUID uuid, Punishment punishment, BanStatus status) {
		return new Ban(uuid,
				punishment.getReason(),
				punishment.getOperator(),
				BanType.UNKNOW,
				punishment.getEnd(),
				punishment.getReason(),
				null,
				status,
				punishment.getStart(),
				punishment.isExpired() ? -1 : System.currentTimeMillis());
	}
	
	public static class Provider implements BanProcessorProvider, PluginDependentExtension {
		
		@Override
		public String getId() {
			return "advancedban";
		}
		
		@Override
		public BanProcessor create(Adapter adapter) {
			return new AdvancedBanProcessor();
		}
		
		@Override
		public String getPluginId() {
			return "AdvancedBan";
		}
	}
}
