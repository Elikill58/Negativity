package com.elikill58.negativity.universal.ban.processor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;

public class SpongeBanProcessor implements BanProcessor {

	@Nullable
	@Override
	public ActiveBan executeBan(ActiveBan ban) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return null;
		}

		Instant expirationDate = ban.isDefinitive() ? null : Instant.ofEpochMilli(ban.getExpirationTime());
		Ban spongeBan = Ban.builder()
				.type(BanTypes.PROFILE)
				.profile(GameProfile.of(ban.getPlayerId()))
				.reason(TextSerializers.FORMATTING_CODE.deserialize(ban.getReason()))
				.expirationDate(expirationDate)
				.source(TextSerializers.FORMATTING_CODE.deserialize(ban.getBannedBy()))
				.build();
		banService.addBan(spongeBan);

		NegativityPlayer player = Adapter.getAdapter().getNegativityPlayer(ban.getPlayerId());
		if (player != null) {
			player.banEffect();
			String formattedExpTime = new Timestamp(ban.getExpirationTime()).toString().split("\\.", 2)[0];
			player.kickPlayer(ban.getReason(), formattedExpTime, ban.getBannedBy(), ban.isDefinitive());
		}

		return ban;
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return null;
		}

		GameProfile profile = GameProfile.of(playerId);
		Optional<Ban.Profile> existingBan = banService.getBanFor(profile);
		if (!existingBan.isPresent() || !banService.pardon(profile)) {
			return null;
		}

		Ban.Profile revokedBan = existingBan.get();
		String reason = revokedBan.getReason().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		String bannedBy = revokedBan.getBanSource().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		long expirationTime = revokedBan.getExpirationDate().map(Instant::toEpochMilli).orElse(-1L);
		return new LoggedBan(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null, true);
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
	public ActiveBan getActiveBan(UUID playerId) {
		BanService banService = Sponge.getServiceManager().provide(BanService.class).orElse(null);
		if (banService == null) {
			return null;
		}

		Optional<Ban.Profile> existingBan = banService.getBanFor(GameProfile.of(playerId));
		if (!existingBan.isPresent()) {
			return null;
		}

		Ban.Profile activeBan = existingBan.get();
		String reason = activeBan.getReason().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		String bannedBy = activeBan.getBanSource().map(TextSerializers.FORMATTING_CODE::serialize).orElse("");
		long expirationTime = activeBan.getExpirationDate().map(Instant::toEpochMilli).orElse(-1L);
		return new ActiveBan(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, null);
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		return Collections.emptyList();
	}
}
