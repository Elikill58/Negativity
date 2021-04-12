package com.elikill58.negativity.sponge.commands.child;

import static org.spongepowered.api.command.args.GenericArguments.player;

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
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class ClearCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player target = args.<Player>getOne("target").orElse(null);
		if (target == null) {
			throw new CommandException(Messages.getMessage(src, "only_player"));
		}
		NegativityAccount account = NegativityAccount.get(target.getUniqueId());
		for (Cheat c : Cheat.values()) {
			account.setWarnCount(c, 0);
		}
		Adapter.getAdapter().getAccountManager().update(account);
		Messages.sendMessage(src, "negativity.cleared", "%name%", target.getName());
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return new NegativityCmdWrapper(CommandSpec.builder().executor(new ClearCommand())
				.arguments(player(Text.of("target"))).permission("negativity.alert").build(), false, Perm.MOD);
	}

}
