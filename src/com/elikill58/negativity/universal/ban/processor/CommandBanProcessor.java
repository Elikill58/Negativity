package com.elikill58.negativity.universal.ban.processor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;

public class CommandBanProcessor implements BanProcessor {

	private final List<String> banCommands;
	private final List<String> unbanCommands;

	public CommandBanProcessor(List<String> banCommands, List<String> unbanCommands) {
		this.banCommands = banCommands;
		this.unbanCommands = unbanCommands;
	}

	@Nullable
	@Override
	public ActiveBan executeBan(ActiveBan ban) {
		Adapter adapter = Adapter.getAdapter();
		banCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, ban.getPlayerId(), ban.getReason())));
		return ban;
	}

	@Nullable
	@Override
	public LoggedBan revokeBan(UUID playerId) {
		Adapter adapter = Adapter.getAdapter();
		unbanCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, playerId, "Unknown")));
		return new LoggedBan(playerId, "Unknown", "Unknown", BanType.UNKNOW, 0, null, true);
	}

	@Nullable
	@Override
	public ActiveBan getActiveBan(UUID playerId) {
		return null;
	}

	@Override
	public List<LoggedBan> getLoggedBans(UUID playerId) {
		return Collections.emptyList();
	}

	private static String applyPlaceholders(String rawCommand, UUID playerId, String reason) {
		String life = "?";
		String name = "???";
		String level = "?";
		String gamemode = "?";
		String walkSpeed = "?";
		NegativityPlayer nPlayer = Adapter.getAdapter().getNegativityPlayer(playerId);
		if (nPlayer != null) {
			life = String.valueOf(nPlayer.getLife());
			name = nPlayer.getName();
			level = String.valueOf(nPlayer.getLevel());
			gamemode = nPlayer.getGameMode();
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
