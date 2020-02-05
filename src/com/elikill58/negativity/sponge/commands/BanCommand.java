package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.bool;
import static org.spongepowered.api.command.args.GenericArguments.firstParsing;
import static org.spongepowered.api.command.args.GenericArguments.longNum;
import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.command.args.GenericArguments.remainingJoinedStrings;

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
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;

public class BanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null)
			throw new CommandException(Messages.getMessage(src, "only_player"));

		boolean isBanDefinitive = args.<Boolean>getOne("definitive").orElse(false);
		long expiration = -1;
		if (!isBanDefinitive)
			expiration = System.currentTimeMillis() + args.<Long>getOne("duration").orElse(-1L);

		String reason = args.requireOne("reason");

		BanType banType = src instanceof Player ? BanType.MOD : BanType.CONSOLE;
		BanManager.executeBan(new ActiveBan(targetPlayer.getUniqueId(), reason, src.getName(), banType, expiration, getFromReason(reason)));

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
						firstParsing(bool(Text.of("definitive")), longNum(Text.of("duration"))),
						remainingJoinedStrings(Text.of("reason")))
				.build();
		return new NegativityCmdWrapper(command, false, "ban");
	}
}
