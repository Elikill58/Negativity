package com.elikill58.negativity.sponge.commands;

import java.util.Collection;
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
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.permissions.Perm;

public class SuspectCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Collection<Player> suspects = args.getAll("suspect");
		if (suspects.isEmpty())
			throw new CommandException(Messages.getMessage(src, "suspect.no_suspects_set"));

		Collection<Cheat> cheats = args.getAll("cheat");

		boolean canReceiveMessage = !(src instanceof Player) || Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer((Player) src), "mod");
		String suspectsList = suspects.stream().map(Player::getName).collect(Collectors.joining(", "));
		if (cheats.isEmpty() || cheats.contains(Cheat.ALL)) {
			for (Player suspect : suspects) {
				SpongeNegativityPlayer.getNegativityPlayer(suspect).startAllAnalyze();
			}

			if (canReceiveMessage) {
				Messages.sendMessage(src, "suspect.starting_full_analysis", "%suspects%", suspectsList);
			}
		} else {
			String cheatsList = cheats.stream().map(Cheat::getName).collect(Collectors.joining(", "));
			if (canReceiveMessage) {
				Messages.sendMessage(src, "suspect.starting_analysis", "%suspects%", suspectsList, "%cheats%", cheatsList);
			}

			for (Player suspect : suspects) {
				SuspectManager.analyzeText(SpongeNegativityPlayer.getNegativityPlayer(suspect), cheats);
			}
		}

		return CommandResult.success();
	}
}
