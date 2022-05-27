package com.elikill58.negativity.sponge;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.ban.BanTypes;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
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

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SpongeBanProcessor implements BanProcessor {
	
	@Override
	public BanResult executeBan(Ban ban) {
		BanService banService = Sponge.server().serviceProvider().banService();
		Instant expirationDate = ban.isDefinitive() ? null : Instant.ofEpochMilli(ban.getExpirationTime());
		org.spongepowered.api.service.ban.Ban spongeBan = org.spongepowered.api.service.ban.Ban.builder()
			.type(BanTypes.PROFILE)
			.profile(GameProfile.of(ban.getPlayerId()))
			.reason(LegacyComponentSerializer.legacyAmpersand().deserialize(ban.getReason()))
			.expirationDate(expirationDate)
			.source(LegacyComponentSerializer.legacyAmpersand().deserialize(ban.getBannedBy()))
			.build();
		banService.add(spongeBan);
		
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player != null) {
			BanUtils.kickForBan(player, ban);
		}
		
		return new BanResult(BanResultType.DONE, ban);
	}
	
	@Nullable
	@Override
	public BanResult revokeBan(UUID playerId) {
		BanService banService = Sponge.server().serviceProvider().banService();
		GameProfile profile = GameProfile.of(playerId);
		try {
			Optional<org.spongepowered.api.service.ban.Ban.Profile> existingBan = banService.find(profile).get();
			if (!existingBan.isPresent() || !banService.pardon(profile).get()) {
				return null;
			}
			
			return new BanResult(toNegativityRevokedBan(existingBan.get()));
		} catch (InterruptedException | ExecutionException e) {
			Adapter.getAdapter().getLogger().error("Failed to get or revoke ban of player " + playerId);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean isBanned(UUID playerId) {
		try {
			return Sponge.server().serviceProvider().banService().find(GameProfile.of(playerId)).get().isPresent();
		} catch (InterruptedException | ExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not determine if player " + playerId + " is banned");
			e.printStackTrace();
			return false;
		}
	}
	
	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		BanService banService = Sponge.server().serviceProvider().banService();
		try {
			return banService.find(GameProfile.of(playerId)).get()
				.map(SpongeBanProcessor::toNegativityActiveBan)
				.orElse(null);
		} catch (InterruptedException | ExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not get active ban of player " + playerId);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Ban> getAllBans() {
		try {
			BanService banService = Sponge.server().serviceProvider().banService();
			return banService.bans().thenApply(bans -> {
				List<Ban> negativityBans = new ArrayList<>();
				for (org.spongepowered.api.service.ban.Ban ban : bans) {
					if (ban instanceof org.spongepowered.api.service.ban.Ban.Profile) {
						negativityBans.add(toNegativityActiveBan((org.spongepowered.api.service.ban.Ban.Profile) ban));
					}
				}
				return negativityBans;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			Adapter.getAdapter().getLogger().error("Could not get all active bans");
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	private static Ban toNegativityActiveBan(org.spongepowered.api.service.ban.Ban.Profile ban) {
		return toNegativityBan(ban, BanStatus.ACTIVE, -1);
	}
	
	private static Ban toNegativityRevokedBan(org.spongepowered.api.service.ban.Ban.Profile ban) {
		return toNegativityBan(ban, BanStatus.REVOKED, System.currentTimeMillis());
	}
	
	private static Ban toNegativityBan(org.spongepowered.api.service.ban.Ban.Profile ban, BanStatus banStatus, long revocationTime) {
		UUID playerId = ban.profile().uuid();
		String reason = ban.reason().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		String bannedBy = ban.banSource().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		long expirationTime = ban.expirationDate().map(Instant::toEpochMilli).orElse(-1L);
		long executionTime = ban.creationDate().toEpochMilli();
		return new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null, null, banStatus, executionTime, revocationTime);
	}
	
	public static class Provider implements BanProcessorProvider, PlatformDependentExtension {
		
		@Override
		public String getId() {
			return "sponge";
		}
		
		@Nullable
		@Override
		public BanProcessor create(Adapter adapter) {
			return new SpongeBanProcessor();
		}
		
		@Override
		public Platform getPlatform() {
			return Platform.SPONGE8;
		}
	}

	@Override
	public String getName() {
		return "Sponge 8";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from Sponge 8 platform.", "", ChatColor.RED + "Not available:", "&7- Bans on same IP", "&7- Logged bans");
	}
}
