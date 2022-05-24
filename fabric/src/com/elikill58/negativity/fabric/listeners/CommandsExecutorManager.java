package com.elikill58.negativity.fabric.listeners;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.universal.Adapter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class CommandsExecutorManager implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {

	private final String cmd;
	
	public CommandsExecutorManager(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String message = context.getInput();
		String[] args = message.isEmpty() ? new String[0] : message.split(" ");
		String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandExecutionEvent event = new CommandExecutionEvent(cmd, FabricEntityManager.getExecutor(context.getSource()), args, prefix);
		EventManager.callEvent(event);
		return event.hasGoodResult() ? Command.SINGLE_SUCCESS : 0;
	}

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
			SuggestionsBuilder builder) throws CommandSyntaxException {
		String[] arg = context.getInput().split(" ");
		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		TabExecutionEvent event = new TabExecutionEvent(cmd, FabricEntityManager.getExecutor(context.getSource()), arg, prefix);
		EventManager.callEvent(event);
		Adapter.getAdapter().getLogger().info("Suggest: " + context.getInput() + " tab: " + event.getTabContent());
		event.getTabContent().forEach(builder::suggest);
		
		return builder.buildFuture();
	}
}
