package com.elikill58.negativity.sponge8.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.sponge8.impl.entity.SpongeEntityManager;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class NegativityCommandWrapper implements Command.Raw {
	
	private final String cmd;
	
	public NegativityCommandWrapper(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
		String[] args = arguments.getInput().split(" ");
		String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandSender executor = SpongeEntityManager.getExecutor(cause.getCause().first(Audience.class)
			.orElseThrow(() -> new CommandException(Component.text("Could not find appropriate command executor"))));
		CommandExecutionEvent event = new CommandExecutionEvent(cmd, executor, args, prefix);
		EventManager.callEvent(event);
		return event.hasGoodResult() ? CommandResult.success() : CommandResult.empty();
	}
	
	@Override
	public List<String> getSuggestions(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
		String[] args = arguments.getInput().split(" ");
		if (arguments.getInput().endsWith(" ")) {
			args = Arrays.copyOf(args, args.length + 1);
			args[args.length - 1] = "";
		}
		String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandSender executor = SpongeEntityManager.getExecutor(cause.getCause().first(Audience.class)
			.orElseThrow(() -> new CommandException(Component.text("Could not find appropriate command executor"))));
		TabExecutionEvent event = new TabExecutionEvent(cmd, executor, args, prefix);
		EventManager.callEvent(event);
		return event.getTabContent();
	}
	
	@Override
	public boolean canExecute(CommandCause cause) {
		return true;
	}
	
	@Override
	public Optional<Component> getShortDescription(CommandCause cause) {
		return Optional.empty();
	}
	
	@Override
	public Optional<Component> getExtendedDescription(CommandCause cause) {
		return Optional.empty();
	}
	
	@Override
	public Component getUsage(CommandCause cause) {
		return Component.empty();
	}
}
