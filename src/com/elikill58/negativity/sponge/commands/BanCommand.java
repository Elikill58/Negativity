package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.firstParsing;
import static org.spongepowered.api.command.args.GenericArguments.literal;
import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;
import static org.spongepowered.api.command.args.GenericArguments.string;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target")
				.orElseThrow(() -> new CommandException(Messages.getMessage(src, "only_player")));

		boolean isBanDefinitive = args.hasAny("definitive");
		long expiration = -1;
		if (!isBanDefinitive) {
			String duration = args.<String>getOne("duration")
					.orElseThrow(() -> new CommandException(Messages.getMessage(src, "ban.help"), true));
			try {
				expiration = System.currentTimeMillis() + UniversalUtils.parseDuration(duration) * 1000;
			} catch (IllegalArgumentException e) {
				throw new CommandException(Text.of(e.getMessage()), e, true);
			}
		}

		String reason = args.requireOne("reason");

		BanType banType = src instanceof Player ? BanType.MOD : BanType.CONSOLE;
		BanManager.executeBan(new Ban(targetPlayer.getUniqueId(), reason, src.getName(), banType, expiration, getFromReason(reason), BanStatus.ACTIVE));

		Messages.sendMessage(src, "ban.well_ban", "%name%", targetPlayer.getName(), "%reason%", reason);
		return CommandResult.success();
	}

	private String getFromReason(String line) {
		for (String s : line.split(" "))
			for (Cheat c : Cheat.values())
				if (c.getName().equalsIgnoreCase(s) || c.getKey().equalsIgnoreCase(s))
					return c.getName();

		return "mod";
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new BanCommand())
				.arguments(player(Text.of("target")),
						firstParsing(literal(Text.of("definitive"), "definitive"), string(Text.of("duration"))),
						remainingJoinedStrings(Text.of("reason")))
				.build();
		return new NegativityCmdWrapper(command, false, Perm.BAN);
	}
}
