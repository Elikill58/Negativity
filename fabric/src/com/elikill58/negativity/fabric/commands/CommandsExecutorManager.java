package com.elikill58.negativity.fabric.commands;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.fabric.GlobalFabricNegativity;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

/**
 * Brigadier command bridge to Negativity events.
 * <p>
 * Kept generic on the command source type so that this class never references a Minecraft class:
 * it is shipped once in Negativity and must load on every mapping namespace (see
 * {@link GlobalFabricNegativity}).
 *
 * @param <S> the command source type of the running Minecraft version
 */
public class CommandsExecutorManager<S> implements Command<S>, SuggestionProvider<S> {

	private final String cmd;

	public CommandsExecutorManager(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public int run(CommandContext<S> context) throws CommandSyntaxException {
		String input = context.getInput();
		String[] args = input.split(" ");
		args = Arrays.copyOfRange(args, 1, args.length);
		String prefix = "";
		if (args.length > 0) {
			prefix = args[args.length - 1];
		} else {
			args = new String[]{""};
		}
		CommandExecutionEvent event = new CommandExecutionEvent(cmd, GlobalFabricNegativity.getExecutor(context.getSource()), args, prefix);
		EventManager.callEvent(event);
		return event.hasGoodResult() ? Command.SINGLE_SUCCESS : 0;
	}

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) throws CommandSyntaxException {
		String input = context.getInput();
		String[] args = input.split(" ");
		args = Arrays.copyOfRange(args, 1, args.length); // remove first item
		String prefix = "";
		if (args.length > 0) {
			prefix = args[args.length - 1];
		} else {
			args = new String[]{""};
		}
		TabExecutionEvent event = new TabExecutionEvent(cmd, GlobalFabricNegativity.getExecutor(context.getSource()), args, prefix);
		EventManager.callEvent(event);
		event.getTabContent().forEach(builder::suggest);

		return builder.buildFuture();
	}
}
