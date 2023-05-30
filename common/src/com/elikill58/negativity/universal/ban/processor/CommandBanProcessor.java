package com.elikill58.negativity.universal.ban.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CommandBanProcessor implements BanProcessor {

	private final List<String> banCommands;
	private final List<String> unbanCommands;

	public CommandBanProcessor(List<String> banCommands, List<String> unbanCommands) {
		this.banCommands = banCommands;
		this.unbanCommands = unbanCommands;
	}

	@Override
	public BanResult executeBan(Ban ban) {
		Adapter adapter = Adapter.getAdapter();
		NegativityPlayer np = NegativityPlayer.getCached(ban.getPlayerId());
		banCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, ban.getPlayerId(), np, ban.getReason())));
		return new BanResult(ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		Adapter adapter = Adapter.getAdapter();
		NegativityPlayer np = NegativityPlayer.getCached(playerId);
		unbanCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, playerId, np, "Unknown")));
		return new BanResult(new Ban(playerId, "Unknown", "Unknown", SanctionnerType.UNKNOW, 0, null, null, BanStatus.REVOKED, -1, System.currentTimeMillis()));
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		return null;
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
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "Command";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Use command to ban/unban.", "", "&cNot available:", "&6Everything that need to get data.", "&7- Active bans", "&7- All bans",
				"&7- Ban on same IP", "&7- Logged/Old bans");
	}

	private static String applyPlaceholders(String rawCommand, UUID playerId, NegativityPlayer np, String reason) {
		String life = "?";
		String name = "???";
		int level = 0;
		String gamemode = "?";
		String walkSpeed = "?";
		Player p = np == null ? NegativityPlayer.getPlayer(playerId) : np.getPlayer();
		if (p != null) {
			life = String.format("%.2f", p.getHealth());
			name = p.getName();
			level = p.getLevel();
			gamemode = p.getGameMode().getName();
			walkSpeed = String.format("%.2f", p.getWalkSpeed());
		}
		return UniversalUtils.replacePlaceholders(rawCommand, "%uuid%", playerId.toString(), "%name%", name, "%reason%", reason, "%life%", life, "%level%", level, "%gm%", gamemode,
				"%walk_speed%", walkSpeed, "%alert%", np == null ? "" : np.getReason(null));
	}
}
