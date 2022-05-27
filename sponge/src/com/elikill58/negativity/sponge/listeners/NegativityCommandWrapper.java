package com.elikill58.negativity.sponge.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class NegativityCommandWrapper implements Command.Raw {
	
	private final String cmd;
	
	public NegativityCommandWrapper(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
		String[] args = arguments.input().split(" ");
		String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandSender executor = SpongeEntityManager.getExecutor(cause.cause().first(Audience.class)
			.orElseThrow(() -> new CommandException(Component.text("Could not find appropriate command executor"))));
		CommandExecutionEvent event = new CommandExecutionEvent(cmd, executor, args, prefix);
		EventManager.callEvent(event);
		return CommandResult.success(); // TODO support bad results ?
	}
	
	@Override
	public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
		String[] args = arguments.input().split(" ");
		if (arguments.input().endsWith(" ")) {
			args = Arrays.copyOf(args, args.length + 1);
			args[args.length - 1] = "";
		}
		String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandSender executor = SpongeEntityManager.getExecutor(cause.cause().first(Audience.class)
			.orElseThrow(() -> new CommandException(Component.text("Could not find appropriate command executor"))));
		TabExecutionEvent event = new TabExecutionEvent(cmd, executor, args, prefix);
		EventManager.callEvent(event);
		
		List<String> suggestions = event.getTabContent();
		List<CommandCompletion> completions = new ArrayList<>(suggestions.size());
		for (String suggestion : suggestions) {
			completions.add(CommandCompletion.of(suggestion));
		}
		return completions;
	}
	
	@Override
	public boolean canExecute(CommandCause cause) {
		return true;
	}
	
	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.empty();
	}
	
	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.empty();
	}
	
	@Override
	public Component usage(CommandCause cause) {
		return Component.empty();
	}
}
