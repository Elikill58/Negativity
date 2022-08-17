package com.elikill58.negativity.universal.warn.processor.hook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;

public class CommandWarnProcessor implements WarnProcessor {

	private final List<String> warnCommands;
	private final List<String> unWarnCommands;

	public CommandWarnProcessor(List<String> warnCommands, List<String> unWarnCommands) {
		this.warnCommands = warnCommands;
		this.unWarnCommands = unWarnCommands;
	}

	@Override
	public WarnResult executeWarn(Warn ban) {
		Adapter adapter = Adapter.getAdapter();
		warnCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, ban.getPlayerId(), ban.getReason())));
		return new WarnResult(ban);
	}

	@Override
	public WarnResult revokeWarn(UUID playerId) {
		Adapter adapter = Adapter.getAdapter();
		unWarnCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, playerId, "Unknown")));
		return new WarnResult(new Warn(playerId, "Unknown", "Unknown", SanctionnerType.UNKNOW, null, -1));
	}

	@Override
	public WarnResult revokeWarn(Warn warn) {
		Adapter adapter = Adapter.getAdapter();
		unWarnCommands.forEach(cmd -> adapter.runConsoleCommand(applyPlaceholders(cmd, warn.getPlayerId(), "Unknown")));
		return new WarnResult(warn);
	}

	@Override
	public List<Warn> getActiveWarn(UUID playerId) {
		return Collections.emptyList();
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		return Collections.emptyList();
	}

	@Override
	public List<Warn> getAllWarns() {
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
