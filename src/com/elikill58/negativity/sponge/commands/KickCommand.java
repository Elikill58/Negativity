package com.elikill58.negativity.sponge.commands;

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
import com.elikill58.negativity.universal.permissions.Perm;

public class KickCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target")
				.orElseThrow(() -> new CommandException(Messages.getMessage(src, "only_player")));

		String reason = args.requireOne("reason");
		targetPlayer.kick(Messages.getMessage(targetPlayer, "kick.kicked", "%name%", src.getName(), "%reason%", reason));
		Messages.sendMessage(src, "kick.well_kick", "%name%", targetPlayer.getName(), "%reason%", reason);
		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new KickCommand())
				.arguments(player(Text.of("target")), remainingJoinedStrings(Text.of("reason")))
				.build();
		return new NegativityCmdWrapper(command, false, Perm.BAN);
	}
}
