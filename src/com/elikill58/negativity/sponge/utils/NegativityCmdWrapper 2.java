package com.elikill58.negativity.sponge.utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.permissions.Perm;

/**
 * A {@link CommandCallable} wrapper to handle Negativity's permission system.
 */
public class NegativityCmdWrapper implements CommandCallable {

	private final CommandCallable delegate;
	private final boolean requiresPlayerSender;
	@Nullable
	private final String negativityPermission;

	public NegativityCmdWrapper(CommandCallable delegate, boolean requiresPlayerSender, @Nullable String negativityPermission) {
		this.delegate = delegate;
		this.requiresPlayerSender = requiresPlayerSender;
		this.negativityPermission = negativityPermission;
	}

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if (requiresPlayerSender && !(source instanceof Player)) {
			throw new CommandException(Messages.getMessage(source, "only_player"));
		}

		if (negativityPermission != null && source instanceof Player) {
			SpongeNegativityPlayer negativityPlayer = SpongeNegativityPlayer.getNegativityPlayer((Player) source);
			if (!Perm.hasPerm(negativityPlayer, negativityPermission)) {
				throw new CommandPermissionException(Messages.getMessage(source, "not_permission"));
			}
		}

		return delegate.process(source, arguments);
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
		if (!testPermission(source)) {
			return Collections.emptyList();
		}

		return delegate.getSuggestions(source, arguments, targetPosition);
	}

	@Override
	public boolean testPermission(CommandSource source) {
		if (requiresPlayerSender && !(source instanceof Player)) {
			return false;
		}

		if (negativityPermission != null && source instanceof Player) {
			return ((Player) source).hasPermission("negativity." + negativityPermission);
		}

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
