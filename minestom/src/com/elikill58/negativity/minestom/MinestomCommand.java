package com.elikill58.negativity.minestom;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class MinestomCommand extends Command implements CommandExecutor {

	public MinestomCommand(String cmd, String... alias) {
		super(cmd, alias);
		setDefaultExecutor(this);
		addSyntax((sender, context) -> {}, ArgumentType.StringArray(cmd + "-args").setSuggestionCallback(this::tabCompletion));
	}

	public void tabCompletion(CommandSender sender, CommandContext context, Suggestion suggestion) {
		String input = context.getInput().replace("\0", ""); // "\0" is "NUL" char
		String[] args = input.split(" ");
		args = Arrays.copyOfRange(args, 1, args.length); // remove first item
		String prefix = "";
		if (args.length > 0) {
			prefix = args[args.length - 1];
		} else {
			args = new String[] { "" };
		}
		TabExecutionEvent tab = new TabExecutionEvent(getName(), MinestomEntityManager.getExecutor(sender), args,
				prefix);
		EventManager.callEvent(tab);
		tab.getTabContent().stream().map(SuggestionEntry::new).forEach(suggestion::addEntry);
	}

	@Override
	public void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
		String input = context.getInput();
		String[] args = input.split(" ");
		args = Arrays.copyOfRange(args, 1, args.length);
		String prefix = "";
		if (args.length > 0) {
			prefix = args[args.length - 1];
		} else {
			args = new String[] { "" };
		}
		EventManager.callEvent(
				new CommandExecutionEvent(getName(), MinestomEntityManager.getExecutor(sender), args, prefix));
	}
}
