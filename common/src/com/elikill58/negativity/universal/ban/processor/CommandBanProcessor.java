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
		banCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, ban.getPlayerId(), ban.getReason())));
		return new BanResult(ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		Adapter adapter = Adapter.getAdapter();
		unbanCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, playerId, "Unknown")));
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
		return Arrays.asList(ChatColor.YELLOW + "Use command to ban/unban.", "", "&cNot available:", "&6Everything that need to get data.",
				"&7- Active bans", "&7- All bans", "&7- Ban on same IP", "&7- Logged/Old bans");
	}

	private static String applyPlaceholders(String rawCommand, UUID playerId, String reason) {
		String life = "?";
		String name = "???";
		String level = "?";
		String gamemode = "?";
		String walkSpeed = "?";
		Player nPlayer = NegativityPlayer.getCached(playerId).getPlayer();
		if (nPlayer != null) {
			life = String.valueOf(nPlayer.getHealth());
			name = nPlayer.getName();
			level = String.valueOf(nPlayer.getLevel());
			gamemode = nPlayer.getGameMode().getName();
			walkSpeed = String.valueOf(nPlayer.getWalkSpeed());
		}
		return rawCommand.replace("%uuid%", playerId.toString())
				.replace("%name%", name)
				.replace("%reason%", reason)
				.replace("%life%", life)
				.replace("%level%", level)
				.replace("%gm%", gamemode)
				.replace("%walk_speed%", walkSpeed);
	}
}
