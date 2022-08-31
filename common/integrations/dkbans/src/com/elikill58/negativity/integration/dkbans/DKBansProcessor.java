package com.elikill58.negativity.integration.dkbans;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;

public class DKBansProcessor implements BanProcessor, WarnProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		DKBansPlayer player = DKBans.getInstance().getPlayerManager().getPlayer(ban.getPlayerId());
		PlayerHistoryEntrySnapshotBuilder builder = player.punish().punishmentType(PunishmentType.BAN).reason(ban.getReason());
		if(!ban.isDefinitive())
			builder.duration(Duration.ofMillis(ban.getRevocationTime() - System.currentTimeMillis()));
		if(ban.getBannedByUUID() != null) {
			builder.staff(DKBans.getInstance().getPlayerManager().getPlayer(ban.getBannedByUUID()));
		} else
			builder.staff(DKBansExecutor.CONSOLE);
		builder.execute();
		return new BanResult(BanResultType.DONE);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		DKBans.getInstance().getPlayerManager().getPlayer(playerId).unpunish(DKBansExecutor.CONSOLE, PunishmentType.BAN);
		return new BanResult(BanResultType.DONE);
	}

	@Override
	public Ban getActiveBan(UUID playerId) {
		DKBansPlayer player = DKBans.getInstance().getPlayerManager().getPlayer(playerId);
		PlayerHistoryEntry entry = player.getHistory().getActiveEntry(PunishmentType.BAN);
		return getBan(entry.getFirst());
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		List<Ban> list = new ArrayList<Ban>();
		DKBans.getInstance().getPlayerManager().getPlayer(playerId).getHistory().getEntries(PunishmentType.BAN).forEach(h -> {
			h.getAll().stream().map(this::getBan).forEach(list::add);
		});
		return list;
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		List<Ban> list = new ArrayList<>();
		DKBans.getInstance().getPlayerManager().getPlayers(ip).stream().map(DKBansPlayer::getUniqueId).map(this::getLoggedBans).forEach(list::addAll);
		return list;
	}
	
	@Override
	public List<Ban> getAllBans() {
		List<Ban> list = new ArrayList<>();
		DKBans.getInstance().getPlayerManager().getLoadedPlayers().stream().map(DKBansPlayer::getUniqueId).map(this::getLoggedBans).forEach(list::addAll);
		return list;
	}

	@Override
	public WarnResult executeWarn(Warn warn) {
		DKBansPlayer player = DKBans.getInstance().getPlayerManager().getPlayer(warn.getPlayerId());
		PlayerHistoryEntrySnapshotBuilder builder = player.punish().punishmentType(PunishmentType.WARN).reason(warn.getReason());
		if(warn.getWarnedByUUID() != null) {
			builder.staff(DKBans.getInstance().getPlayerManager().getPlayer(warn.getWarnedByUUID()));
		} else
			builder.staff(DKBansExecutor.CONSOLE);
		builder.execute();
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(UUID playerId, String revoker) {
		UUID revokerUUID = Adapter.getAdapter().getUUID(revoker);
		DKBans.getInstance().getPlayerManager().getPlayer(playerId).unpunish(revokerUUID == null ? DKBansExecutor.CONSOLE : DKBans.getInstance().getPlayerManager().getPlayer(revokerUUID), PunishmentType.WARN);
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public WarnResult revokeWarn(Warn warn, String revoker) {
		UUID revokerUUID = Adapter.getAdapter().getUUID(revoker);
		DKBans.getInstance().getPlayerManager().getPlayer(warn.getPlayerId()).unpunish(revokerUUID == null ? DKBansExecutor.CONSOLE : DKBans.getInstance().getPlayerManager().getPlayer(revokerUUID), PunishmentType.WARN);
		return new WarnResult(WarnResultType.DONE);
	}

	@Override
	public List<Warn> getWarn(UUID playerId) {
		List<Warn> list = new ArrayList<>();
		DKBans.getInstance().getPlayerManager().getPlayer(playerId).getHistory().getEntries(PunishmentType.WARN).forEach(h -> {
			h.getAll().stream().map(this::getWarn).forEach(list::add);
		});
		return list;
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		List<Warn> list = new ArrayList<>();
		DKBans.getInstance().getPlayerManager().getPlayers(ip).stream().map(DKBansPlayer::getUniqueId).map(this::getWarn).forEach(list::addAll);
		return list;
	}
	
	@Override
	public String getName() {
		return "DKBans";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from DKBans plugin");
	}

	private Ban getBan(PlayerHistoryEntrySnapshot h) {
		return new Ban(h.getScope().getId(), h.getReason(), h.getStaff().getName(),
				h.getStaff().isPlayer() ? SanctionnerType.MOD : SanctionnerType.CONSOLE,
				h.getTimeout(), null, null, h.isActive() ? BanStatus.ACTIVE : (h.getRevokeReason() != null ? BanStatus.REVOKED : BanStatus.EXPIRED));
	}
	
	private Warn getWarn(PlayerHistoryEntrySnapshot h) {
		return new Warn(h.getId(), h.getScope().getId(), h.getReason(), h.getStaff().getName(), h.getStaff().isPlayer() ? SanctionnerType.MOD : SanctionnerType.CONSOLE, null, h.getTimeout(), h.isActive(), 0, h.getRevokeReason());
	}

	public static class Provider implements BanProcessorProvider, WarnProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "dkbans";
		}

		@Override
		public DKBansProcessor create(Adapter adapter) {
			return new DKBansProcessor();
		}

		@Override
		public String getPluginId() {
			return "DKBans";
		}
	}
}
