package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.utils.NegativityCmdSuggestionsEnhancer;
import com.elikill58.negativity.universal.Cheat;

public class NegativityCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Messages.getMessage(src, "sender_not_a_player"));

		Player playerSource = ((Player) src);
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null) {
			Messages.sendMessageList(playerSource, "negativity.verif.help");
			return CommandResult.empty();
		}

		if (!playerSource.hasPermission("negativity.verif") && !playerSource.hasPermission("negativity.*")) {
			throw new CommandException(Messages.getMessage(playerSource, "not_permission"));
		}

		Inv.openCheckMenu(playerSource, targetPlayer);

		return CommandResult.success();
	}

	public static CommandCallable create() {
		// To work around an undesirable behaviour of arguments completion,
		// we wrap /negativity in a CommandCallable that always suggests online players
		// in addition to the default suggestion results.
		return new NegativityCmdSuggestionsEnhancer(CommandSpec.builder()
				.executor(new NegativityCommand())
				.arguments(GenericArguments.player(Text.of("target")))
				.child(CommandSpec.builder()
						.executor(new NegativityVerifCommand())
						.arguments(GenericArguments.player(Text.of("target")),
								GenericArguments.allOf(GenericArguments.choices(Text.of("cheats"), Cheat.CHEATS_BY_KEY, true, false)))
						.build(), "verif")
				.build());
	}
}
