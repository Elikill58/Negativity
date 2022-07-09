package com.elikill58.negativity.spigot.integration.ultrapunishments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

import me.TechsCode.UltraPunishments.APIEndpoint;
import me.TechsCode.UltraPunishments.UltraPunishments;
import me.TechsCode.UltraPunishments.storage.types.IndexedPlayer;
import me.TechsCode.UltraPunishments.storage.types.Punishment;
import me.TechsCode.UltraPunishments.storage.types.PunishmentType;
import me.TechsCode.UltraPunishments.tools.PunishmentCreator;
import me.TechsCode.UltraPunishments.tools.PunishmentList;
import me.TechsCode.base.storage.Stored;

public class UltraPunishmentsProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		APIEndpoint api = UltraPunishments.getAPI();
		IndexedPlayer up = api.getPlayerIndexes().get(ban.getPlayerId()).orElse(null);
		if(up == null)
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		PunishmentCreator creator = api.newPunishment(up, PunishmentType.BAN).setReason(ban.getReason()).setDuration(System.currentTimeMillis() - ban.getExpirationTime());
		api.getPlayerIndexes().get(ban.getBannedByUUID()).ifPresent(ip -> creator.setIssuer(ip)); // set banned if found
		creator.create();
		BanUtils.kickForBan(NegativityPlayer.getCached(ban.getPlayerId()), ban);
		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		APIEndpoint api = UltraPunishments.getAPI();
		IndexedPlayer up = api.getPlayerIndexes().get(playerId).orElse(null);
		if(up == null)
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		api.getPunishments().target(up).forEach(Punishment::remove);
		return new BanResult(BanResultType.DONE);
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		APIEndpoint api = UltraPunishments.getAPI();
		IndexedPlayer up = api.getPlayerIndexes().get(playerId).orElse(null);
		if(up == null)
			return null;
		PunishmentList list = api.getPunishments().target(up).expired(false);
		return list.isEmpty() ? null : toBan(list.get(0));
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		APIEndpoint api = UltraPunishments.getAPI();
		IndexedPlayer up = api.getPlayerIndexes().get(playerId).orElse(null);
		if(up == null)
			return new ArrayList<>();
		return api.getPunishments().target(up).stream().map(this::toBan).collect(Collectors.toList());
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		APIEndpoint api = UltraPunishments.getAPI();
		return api.getPunishments().stream().filter(pu -> {
			Stored<IndexedPlayer> storedPu = pu.getTarget();
			if(storedPu.isPresent() && storedPu.get().isPresent()) {
				IndexedPlayer p = storedPu.get().get();
				Optional<String> optIp = p.getIP();
				if(optIp.isPresent())
					return optIp.get().equalsIgnoreCase(ip);
			}
			return false;
		}).map(this::toBan).collect(Collectors.toList());
	}

	@Override
	public List<Ban> getAllBans() {
		return UltraPunishments.getAPI().getPunishments().stream().map(this::toBan).collect(Collectors.toList());
	}
	
	private Ban toBan(Punishment pu) {
		IndexedPlayer p = pu.getTarget().get().get();
		// store the IP even if not used by plugin mostly to be able to get ban on same IP
		return new Ban(p.getUuid(), pu.getReason().orElse(null), pu.getIssuerName(), pu.getIssuer().isPresent() ? BanType.MOD : BanType.PLUGIN, 0, null, p.getIP().orElse(null), pu.isExpired() ? BanStatus.EXPIRED : BanStatus.ACTIVE);
	}
	
	@Override
	public String getName() {
		return "Ultra Punishments";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from UltraPunishments Bans plugin.");
	}

	public static class Provider implements BanProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "ultrapunishments";
		}

		@Override
		public BanProcessor create(Adapter adapter) {
			return new UltraPunishmentsProcessor();
		}

		@Override
		public String getPluginId() {
			return "UltraPunishments";
		}
	}
}
