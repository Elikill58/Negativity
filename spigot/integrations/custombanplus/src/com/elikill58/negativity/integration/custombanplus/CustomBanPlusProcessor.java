package com.elikill58.negativity.integration.custombanplus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

import me.coralise.CustomBansPlus;
import me.coralise.Utils;
import me.coralise.API.CBPAPI;
import me.coralise.bans.BanManager;
import me.coralise.bans.BanPreset;
import me.coralise.enums.BanType;
import me.coralise.enums.HistoryStatus;
import me.coralise.enums.Punishment;
import me.coralise.objects.HistoryRecord;
import me.coralise.players.CBPlayer;

public class CustomBanPlusProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}
		CBPlayer cbp = CustomBansPlus.getInstance().plm.getCBPlayer(ban.getPlayerId());
		BanManager bm = CustomBansPlus.getInstance().bm;
		BanPreset sev;
		CommandSender sender = ban.getBanType().equals(SanctionnerType.MOD) ? Bukkit.getPlayer(ban.getBannedByUUID())
				: Bukkit.getConsoleSender();
		if ((sev = bm.getBanPreset(player.getName())) != null) {
			sev.punishPlayer(cbp, sender, Punishment.BAN, ban.getReason(), false);
			return new BanResult(BanResultType.DONE, ban);
		}
		BanType type = ban.isDefinitive() ? BanType.PERM_BAN : BanType.TEMP_BAN;
		bm.ban(cbp, type, ban.getReason(),
				ban.isDefinitive() ? "perm" : new Utils().getTimeRemaining(new Date(ban.getExpirationTime())), sender,
				false, null, CustomBansPlus.getInstance().u.determineBanAnnType(type, ban.getReason()), false);
		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		BanManager bm = CustomBansPlus.getInstance().bm;
		me.coralise.bans.Ban ban = bm.getBan(playerId);
		bm.removeBan(ban, "Unbanned", Bukkit.getConsoleSender());
		return new BanResult(BanResultType.DONE);
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		return getBan(CustomBansPlus.getInstance().bm.getBan(playerId));
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return CustomBansPlus.getInstance().db.getHistories(playerId).stream().map(this::getBan).collect(Collectors.toList());
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return CBPAPI.getApi().getBannedAlts(ip).stream().map(CustomBansPlus.getInstance().bm::getBan).map(this::getBan).collect(Collectors.toList());
	}

	@Override
	public List<Ban> getAllBans() {
		return CustomBansPlus.getInstance().bm.getBans().values().stream().map(this::getBan)
				.collect(Collectors.toList());
	}

	private Ban getBan(me.coralise.bans.Ban b) {
		return new Ban(b.getUuid(), b.getReason(), b.getStaffName(),
				b.getStaff() == null ? SanctionnerType.CONSOLE : SanctionnerType.MOD,
				b.isPermBan() ? -1 : b.getUnbanDate().getTime(), null, b.getIp(), BanStatus.ACTIVE);
	}

	private Ban getBan(HistoryRecord b) {
		CustomBansPlus pl = CustomBansPlus.getInstance();
		SanctionnerType sanctionner = SanctionnerType.CONSOLE;
		String staffName = pl.getConfig().getString("console-name");
		BanStatus status = b.getStatus().equals(HistoryStatus.ACTIVE) ? BanStatus.ACTIVE : (b.getStatus().equals(HistoryStatus.LIFTED) ? BanStatus.EXPIRED : BanStatus.REVOKED);
		if(b.getStaffUuid() != null) {
			sanctionner = SanctionnerType.MOD;
			staffName = pl.plm.getCBPlayer(b.getStaffUuid()).getName();
		}
		long expireDate = b.getUnpunishDate() == null ? -1 : b.getUnpunishDate().getTime();
		return new Ban(b.getUuid(), b.getReason(), staffName, sanctionner, -1, null, null, status, -1, expireDate);
	}

	@Override
	public String getName() {
		return "Custom Bans Plus";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from Custom Bans Plus plugin.");
	}

	public static class Provider implements BanProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "custombanplus";
		}

		@Override
		public BanProcessor create(Adapter adapter) {
			return new CustomBanPlusProcessor();
		}

		@Override
		public String getPluginId() {
			return "CustomBansPlus";
		}
	}
}
