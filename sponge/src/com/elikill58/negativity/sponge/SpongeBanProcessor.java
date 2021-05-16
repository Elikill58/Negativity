package com.elikill58.negativity.sponge;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.BanTypes;

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

public class SpongeBanProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return new BanResult(BanResultType.UNKNOW_SERVICE, null);
		}

		Instant expirationDate = ban.isDefinitive() ? null : Instant.ofEpochMilli(ban.getExpirationTime());
		org.spongepowered.api.util.ban.Ban spongeBan = org.spongepowered.api.util.ban.Ban.builder()
				.type(BanTypes.PROFILE)
				.profile(GameProfile.of(ban.getPlayerId()))
				.reason(TextSerializers.FORMATTING_CODE.deserialize(ban.getReason()))
				.expirationDate(expirationDate)
				.source(TextSerializers.FORMATTING_CODE.deserialize(ban.getBannedBy()))
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
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return new BanResult(BanResultType.UNKNOW_SERVICE);
		}

		GameProfile profile = GameProfile.of(playerId);
		Optional<org.spongepowered.api.util.ban.Ban.Profile> existingBan = banService.getBanFor(profile);
		if (!existingBan.isPresent() || !banService.pardon(profile)) {
			return null;
		}

		org.spongepowered.api.util.ban.Ban.Profile revokedBan = existingBan.get();
		String reason = revokedBan.getReason().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		String bannedBy = revokedBan.getBanSource().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		long expirationTime = revokedBan.getExpirationDate().map(Instant::toEpochMilli).orElse(-1L);
		long executionTime = revokedBan.getCreationDate().toEpochMilli();
		return new BanResult(new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null, null, BanStatus.REVOKED, executionTime, System.currentTimeMillis()));
	}

	@Override
	public boolean isBanned(UUID playerId) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return false;
		}

		return banService.isBanned(GameProfile.of(playerId));
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return null;
		}

		Optional<org.spongepowered.api.util.ban.Ban.Profile> existingBan = banService.getBanFor(GameProfile.of(playerId));
		if (!existingBan.isPresent()) {
			return null;
		}

		org.spongepowered.api.util.ban.Ban.Profile activeBan = existingBan.get();
		String reason = activeBan.getReason().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		String bannedBy = activeBan.getBanSource().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
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
