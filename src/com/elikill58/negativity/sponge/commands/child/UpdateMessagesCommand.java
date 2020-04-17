package com.elikill58.negativity.sponge.commands.child;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.universal.translation.MessagesUpdater;

public class UpdateMessagesCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		MessagesUpdater.performUpdate("messages", (message, placeholder) -> Messages.sendMessage(src, message, (Object[]) placeholder));
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new UpdateMessagesCommand())
				.build();
	}
}
