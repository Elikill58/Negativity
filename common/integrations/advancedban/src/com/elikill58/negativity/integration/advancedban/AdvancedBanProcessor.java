package com.elikill58.negativity.integration.advancedban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

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
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;
import com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import me.leoko.advancedban.utils.SQLQuery;

public class AdvancedBanProcessor implements BanProcessor, WarnProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}

		long endTime = ban.isDefinitive() ? 0 : ban.getExpirationTime();
		PunishmentType type = ban.isDefinitive() ? PunishmentType.BAN : PunishmentType.TEMP_BAN;
		Punishment punishment = new Punishment(player.getName(), UUIDManager.get().getUUID(player.getName()),
				ban.getReason(), ban.getBannedBy(), type, System.currentTimeMillis(), endTime, "", -1);
		// Must be invoked asynchronously because an async event is thrown in there and
		// Bukkit enforces it
		CompletableFuture.runAsync(punishment::create);

		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		Punishment punishment = PunishmentManager.get().getBan(playerId.toString());
		if (punishment == null) {
			return new BanResult(BanResultType.ALREADY_UNBANNED);
		}

		// Must be invoked asynchronously because an async event is thrown in there and
		// Bukkit enforces it
		CompletableFuture.runAsync(punishment::delete);

		return new BanResult(getBan(punishment));
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

		return getBan(punishment);
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(playerId.toString(), PunishmentType.BAN,
				false);
		List<Ban> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> loggedBans.add(getBan(punishment)));
		return loggedBans;
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(SQLQuery.SELECT_USER_PUNISHMENTS_WITH_IP,
				"IP", ip);
		List<Ban> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> {
			if(punishment.getType().name().contains("BAN"))
				loggedBans.add(getBan(punishment));
		});
		return loggedBans;
	}

	@Override
	public List<Ban> getAllBans() {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(SQLQuery.SELECT_ALL_PUNISHMENTS);
		List<Ban> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> loggedBans.add(getBan(punishment)));
		return loggedBans;
	}

	@Override
	public WarnResult executeWarn(Warn warn) {
		Punishment punishment = new Punishment(Adapter.getAdapter().getOfflinePlayer(warn.getPlayerId()).getName(), warn.getPlayerId().toString(),
				warn.getReason(), warn.getWarnedBy(), PunishmentType.WARNING, System.currentTimeMillis(), 0, "", -1);
		// Must be invoked asynchronously because an async event is thrown in there and
		// Bukkit enforces it
		CompletableFuture.runAsync(punishment::create);

		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(UUID playerId, String revoker) {
		PunishmentManager.get().getWarns(playerId.toString()).forEach(p -> p.delete(revoker, true, false));
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(Warn warn, String revoker) {
		Punishment punishment = PunishmentManager.get().getWarn(warn.getId());
		if (punishment == null) {
			return new WarnResult(WarnResultType.NOT_WARNED);
		}
		punishment.delete(revoker, false, false);
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public List<Warn> getWarn(UUID playerId) {
		return PunishmentManager.get().getWarns(playerId.toString()).stream().map(this::getWarn).collect(Collectors.toList());
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		List<Punishment> punishments = PunishmentManager.get().getPunishments(SQLQuery.SELECT_USER_PUNISHMENTS_WITH_IP,
				"IP", ip);
		List<Warn> loggedBans = new ArrayList<>();
		punishments.forEach(punishment -> {
			if(punishment.getType().name().contains("WARNING"))
				loggedBans.add(getWarn(punishment));
		});
		return loggedBans;
	}

	@Override
	public String getName() {
		return "AdvancedBan";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from AdvancedBan plugin.");
	}

	private Ban getBan(Punishment punishment) {
		return new Ban(UUID.fromString(punishment.getUuid()), punishment.getReason(), punishment.getOperator(),
				SanctionnerType.UNKNOW, punishment.getEnd(), punishment.getReason(), null, punishment.isExpired() ? BanStatus.EXPIRED : BanStatus.ACTIVE,
				punishment.getStart(), punishment.getEnd());
	}

	private Warn getWarn(Punishment punishment) {
		return new Warn(punishment.getId(), UUID.fromString(punishment.getUuid()), punishment.getReason(),
				punishment.getOperator(), SanctionnerType.UNKNOW, null, punishment.getStart(), punishment.isExpired(),
				punishment.getEnd(), "?");
	}

	public static class Provider implements BanProcessorProvider, WarnProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "advancedban";
		}

		@Override
		public AdvancedBanProcessor create(Adapter adapter) {
			return new AdvancedBanProcessor();
		}

		@Override
		public String getPluginId() {
			return "AdvancedBan";
		}
	}
}
