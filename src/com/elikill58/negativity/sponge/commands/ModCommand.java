package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Messages.getMessage(src, "sender_not_a_player"));

		Player playerSource = (Player) src;
		if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(playerSource), "mod"))
			throw new CommandException(Messages.getMessage(playerSource, "not_permission"));

		Inv.openModMenu(playerSource);
		return CommandResult.success();
	}
}
