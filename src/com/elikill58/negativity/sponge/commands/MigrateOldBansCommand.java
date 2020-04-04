package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.universal.ban.OldBansDbMigrator;

public class MigrateOldBansCommand implements CommandExecutor {

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new MigrateOldBansCommand())
				.permission("negativity.a_permission_nobody_should_have.use_to_migrate_old_bans")
				.build();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof ConsoleSource)) {
			return CommandResult.empty();
		}

		try {
			OldBansDbMigrator.performMigration();
		} catch (Exception e) {
			Text text = Text.of("An error occurred when performing migration: ", e.getMessage());
			src.sendMessage(text);
			e.printStackTrace();
			throw new CommandException(text, e);
		}

		return CommandResult.success();
	}
}
