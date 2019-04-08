package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.allOf;
import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

public class NegativityVerifCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null) {
			throw new CommandException(Messages.getMessage(src, "not_forget_player"));
		}

		Set<Cheat> cheats = new LinkedHashSet<>(args.getAll("cheats"));
		SpongeNegativityPlayer targetNPlayer = SpongeNegativityPlayer.getNegativityPlayer(targetPlayer);
		if (cheats.isEmpty() || cheats.contains(Cheat.ALL)) {
			targetNPlayer.startAllAnalyze();
			Messages.sendMessage(src, "negativity.verif.start_all", "%name%", targetPlayer.getName());
		} else {
			cheats.forEach(targetNPlayer::startAnalyze);
			String cheatNamesList = cheats.stream().map(Cheat::getName).collect(Collectors.joining(", "));
			Messages.sendMessage(src, "negativity.verif.start", "%name%", targetPlayer.getName(), "%cheat%", cheatNamesList);
		}

		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new NegativityVerifCommand())
				.arguments(player(Text.of("target")),
						allOf(choices(Text.of("cheats"), Cheat.CHEATS_BY_KEY, true, false)))
				.build();
		return new NegativityCmdWrapper(command, false, "verif");
	}
}
