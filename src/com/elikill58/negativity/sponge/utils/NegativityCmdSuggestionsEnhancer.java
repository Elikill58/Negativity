package com.elikill58.negativity.sponge.utils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;

/*
 * Made for the /negativity command that should suggest either its subcommand 'verif' or an online player,
 * but only suggests its subcommand. Unsure if this is an intended behaviour or not but that is definitely not what we want.
 */
public class NegativityCmdSuggestionsEnhancer implements CommandCallable {

	private CommandCallable delegate;

	public NegativityCmdSuggestionsEnhancer(CommandCallable delegate) {
		this.delegate = delegate;
	}

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		return delegate.process(source, arguments);
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
		// Make sure we are completing the first argument
		if (StringUtils.isNotBlank(arguments)) {
			return delegate.getSuggestions(source, arguments, targetPosition);
		}

		// Using a Set prevents duplicate entries
		Set<String> suggestions = Sponge.getServer().getOnlinePlayers()
				.stream()
				.map(Player::getName)
				.collect(Collectors.toSet());
		suggestions.addAll(delegate.getSuggestions(source, arguments, targetPosition));
		return ImmutableList.copyOf(suggestions);
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return delegate.testPermission(source);
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return delegate.getShortDescription(source);
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return delegate.getHelp(source);
	}

	@Override
	public Text getUsage(CommandSource source) {
		return delegate.getUsage(source);
	}
}
