package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.command.args.GenericArguments.requiringPermission;

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
import com.elikill58.negativity.sponge.commands.child.AdminCommand;
import com.elikill58.negativity.sponge.commands.child.AlertCommand;
import com.elikill58.negativity.sponge.commands.child.ModCommand;
import com.elikill58.negativity.sponge.commands.child.ReloadCommand;
import com.elikill58.negativity.sponge.commands.child.VerifCommand;
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.sponge.utils.NegativityCmdSuggestionsEnhancer;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;

public class NegativityCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Messages.getMessage(src, "only_player"));
		}

		Player playerSource = ((Player) src);
		Player targetPlayer = args.<Player>getOne("target").orElse(null);
		if (targetPlayer == null) {
			Messages.sendMessageList(playerSource, "negativity.verif.help");
			return CommandResult.empty();
		}
		AbstractInventory.open(InventoryType.CHECK_MENU, playerSource, targetPlayer);
		return CommandResult.success();
	}

	public static CommandCallable create() {
		// To work around an undesirable behaviour of arguments completion,
		// we wrap /negativity in a CommandCallable that always suggests online players
		// in addition to the default suggestion results.
		NegativityCmdSuggestionsEnhancer command = new NegativityCmdSuggestionsEnhancer(CommandSpec.builder()
				.executor(new NegativityCommand())
				.arguments(requiringPermission(player(Text.of("target")), "negativity.verif"))
				.child(VerifCommand.create(), "verif")
				.child(AlertCommand.create(), "alert")
				.child(ModCommand.create(), "mod")
				.child(AdminCommand.create(), "admin")
				.child(ReloadCommand.create(), "reload")
				.build());
		return new NegativityCmdWrapper(command, false, null);
	}
}
