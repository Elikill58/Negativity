package com.elikill58.negativity.universal.ban.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.annotations.Nullable;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.storage.ActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.BanLogsStorage;

/**
 * This ban processor simply saves bans (active and logged) in a configurable storage ({@link ActiveBanStorage} and {@link BanLogsStorage} respectively).
 * <p>
 * It is important to know its sole purpose is to manage bans, and will not do anything on the game server,
 * like kicking the player when {@link BanProcessor#executeBan(Ban)} executing a ban.
 * If you want direct actions on the game server use {@link NegativityBanProcessor} instead.
 */
public class NegativityBanProcessor implements BanProcessor {

	protected final ActiveBanStorage activeBanStorage;
	@Nullable
	protected final BanLogsStorage banLogsStorage;
	protected final String name;

	public NegativityBanProcessor(ActiveBanStorage activeBanStorage, @Nullable BanLogsStorage banLogsStorage, String name) {
		this.activeBanStorage = activeBanStorage;
		this.banLogsStorage = banLogsStorage;
		this.name = name;
	}

	@Override
	public BanResult executeBan(Ban ban) {
		if (isBanned(ban.getPlayerId())) {
			return new BanResult(BanResultType.ALREADY_BANNED, null);
		}
		activeBanStorage.save(ban);
		NegativityPlayer nPlayer = NegativityPlayer.getCached(ban.getPlayerId());
		// warn: nPlayer will be null if player is offline
		if (nPlayer != null) {
			BanUtils.kickForBan(nPlayer, ban);
		}
		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		Ban activeBan = activeBanStorage.load(playerId);
		if (activeBan == null)
			return new BanResult(BanResultType.ALREADY_UNBANNED);

		activeBanStorage.remove(playerId);
		Ban revokedLoggedBan = Ban.revokedFrom(activeBan, System.currentTimeMillis());

		if (banLogsStorage != null) {
			banLogsStorage.save(revokedLoggedBan);
		}

		return new BanResult(revokedLoggedBan);
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		Ban activeBan = activeBanStorage.load(playerId);
		if (activeBan == null) {
			return null;
		}

		long now = System.currentTimeMillis();
		if (activeBan.isDefinitive() || activeBan.getExpirationTime() > now) {
			return activeBan;
		}

		activeBanStorage.remove(playerId);
		if (banLogsStorage != null) {
			banLogsStorage.save(Ban.from(activeBan, BanStatus.EXPIRED));
		}

		return null;
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		if (banLogsStorage == null) {
			return Collections.emptyList();
		}
		return banLogsStorage.load(playerId);
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		if (activeBanStorage == null) {
			return Collections.emptyList();
		}
		return activeBanStorage.loadBanOnIP(ip);
	}

	@Override
	public List<Ban> getAllBans() {
		return activeBanStorage.getAll();
	}
	
	@Override
	public boolean isHandledByNegativity() {
		return true;
	}
	
	@Override
	public String getName() {
		return "Negativity With " + name;
	}
	
	@Override
	public List<String> getDescription() {
		List<String> desc = new ArrayList<>();
		desc.add(ChatColor.YELLOW + "Processor from Negativity by using " + name);
		boolean hasNotEnabled = false;
		if(banLogsStorage == null) {
			hasNotEnabled = true;
			desc.add("");
			desc.add(ChatColor.RED + "Not available:");
			desc.add("&7- Bans are not logged ('log_bans' in config)");
		}
		if(name.equalsIgnoreCase("file")) {
			if(!hasNotEnabled) {
				desc.add("");
				desc.add(ChatColor.RED + "Not available:");
			}
			desc.add("&7- Bans on same IP");
		}
		return desc;
	}
}
