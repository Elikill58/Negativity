package com.elikill58.negativity.sponge8;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.ban.BanTypes;

import com.elikill58.negativity.api.NegativityPlayer;
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
		BanService banService = Sponge.getServer().getServiceProvider().banService();
		Instant expirationDate = ban.isDefinitive() ? null : Instant.ofEpochMilli(ban.getExpirationTime());
		org.spongepowered.api.service.ban.Ban spongeBan = org.spongepowered.api.service.ban.Ban.builder()
				.type(BanTypes.PROFILE)
				.profile(GameProfile.of(ban.getPlayerId()))
				.reason(LegacyComponentSerializer.legacyAmpersand().deserialize(ban.getReason()))
				.expirationDate(expirationDate)
				.source(LegacyComponentSerializer.legacyAmpersand().deserialize(ban.getBannedBy()))
				.build();
		banService.addBan(spongeBan);

		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player != null) {
			BanUtils.kickForBan(player, ban);
		}

		return new BanResult(BanResultType.DONE, ban);
	}

	@Nullable
	@Override
	public BanResult revokeBan(UUID playerId) {
		BanService banService = Sponge.getServer().getServiceProvider().banService();
		GameProfile profile = GameProfile.of(playerId);
		Optional<org.spongepowered.api.service.ban.Ban.Profile> existingBan = banService.getBanFor(profile);
		if (!existingBan.isPresent() || !banService.pardon(profile)) {
			return null;
		}

		org.spongepowered.api.service.ban.Ban.Profile revokedBan = existingBan.get();
		String reason = revokedBan.getReason().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		String bannedBy = revokedBan.getBanSource().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		long expirationTime = revokedBan.getExpirationDate().map(Instant::toEpochMilli).orElse(-1L);
		long executionTime = revokedBan.getCreationDate().toEpochMilli();
		return new BanResult(new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null, null, BanStatus.REVOKED, executionTime, System.currentTimeMillis()));
	}

	@Override
	public boolean isBanned(UUID playerId) {
		return Sponge.getServer().getServiceProvider().banService().isBanned(GameProfile.of(playerId));
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		BanService banService = Sponge.getServer().getServiceProvider().banService();
		Optional<org.spongepowered.api.service.ban.Ban.Profile> existingBan = banService.getBanFor(GameProfile.of(playerId));
		if (!existingBan.isPresent()) {
			return null;
		}

		org.spongepowered.api.service.ban.Ban.Profile activeBan = existingBan.get();
		String reason = activeBan.getReason().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		String bannedBy = activeBan.getBanSource().map(LegacyComponentSerializer.legacyAmpersand()::serialize).orElse("");
		long expirationTime = activeBan.getExpirationDate().map(Instant::toEpochMilli).orElse(-1L);
		long executionTime = activeBan.getCreationDate().toEpochMilli();
		return new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null, null, BanStatus.ACTIVE, executionTime);
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return Collections.emptyList();
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
			return Platform.SPONGE;
		}
	}
}
