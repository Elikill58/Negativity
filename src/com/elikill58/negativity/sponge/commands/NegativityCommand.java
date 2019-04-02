package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;

public class NegativityCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			return CommandResult.empty();

		Player playerSource = ((Player) src);
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null) {
			Messages.sendMessageList(playerSource, "negativity.verif.help");
			return CommandResult.empty();
		}

		if (!playerSource.hasPermission("negativity.verif") && !playerSource.hasPermission("negativity.*")) {
			Messages.sendMessage(playerSource, "not_permission");
			return CommandResult.empty();
		}

		Inv.openCheckMenu(playerSource, targetPlayer);

		return CommandResult.empty();
	}
}
