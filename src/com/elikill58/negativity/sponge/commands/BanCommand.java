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
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;

public class BanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null)
			throw new CommandException(Messages.getMessage(src, "not_forget_player"));

		boolean isBanDefinitive = args.<Boolean>getOne("definitive").orElse(false);
		long banDuration = 0;
		if (!isBanDefinitive)
			banDuration = args.<Long>getOne("duration").orElse(0L);

		SpongeNegativityPlayer targetNPlayer = SpongeNegativityPlayer.getNegativityPlayer(targetPlayer);
		String reason = args.requireOne("reason");

		BanRequest banRequest = new BanRequest(targetNPlayer, reason, banDuration, isBanDefinitive, src instanceof Player ? BanType.MOD : BanType.CONSOLE, getFromReason(reason), src.getName());
		banRequest.execute();

		Messages.sendMessage(src, "ban.well_ban", "%name%", targetPlayer.getName(), "%reason%", reason);
		return CommandResult.success();
	}

	private String getFromReason(String line) {
		for (String s : line.split(" "))
			for (Cheat c : Cheat.values())
				if (c.getName().equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s))
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
