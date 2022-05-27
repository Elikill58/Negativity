package com.elikill58.negativity.sponge7.listeners;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;

public class CommandsExecutorManager implements CommandCallable {

	private final String cmd;
	
	public CommandsExecutorManager(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public CommandResult process(CommandSource src, String message) throws CommandException {
		String[] args = message.isEmpty() ? new String[0] : message.split(" ");
		String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
		CommandExecutionEvent event = new CommandExecutionEvent(cmd, SpongeEntityManager.getExecutor(src), args, prefix);
		EventManager.callEvent(event);
		return event.hasGoodResult() ? CommandResult.success() : CommandResult.empty();
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return null;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public List<String> getSuggestions(CommandSource src, String message, Location<World> arg2) throws CommandException {
		String[] arg = message.split(" ");
		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		TabExecutionEvent event = new TabExecutionEvent(cmd, SpongeEntityManager.getExecutor(src), arg, prefix);
		EventManager.callEvent(event);
		return event.getTabContent();
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of("");
	}

	@Override
	public boolean testPermission(CommandSource src) {
		return true;
	}
}
