package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ReloadCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		Adapter.getAdapter().reload();
		Messages.sendMessage(src, "negativity.reload_done");
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new ReloadCommand())
				.permission("negativity.reload")
				.build();
	}
}
