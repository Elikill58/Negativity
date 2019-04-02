package com.elikill58.negativity.sponge.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.permissions.Perm;

public class NegativityVerifCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src instanceof Player) {
			Player playerSender = (Player) src;
			if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(playerSender), "verif")) {
				throw new CommandException(Messages.getMessage(playerSender, "not_permission"));
			}
		}

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

		return CommandResult.empty();
	}
}
