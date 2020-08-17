package com.elikill58.negativity.sponge.commands;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.universal.Cheat;
import com.google.common.collect.ImmutableList;

public class PlayersAndCheatsArgument extends CommandElement {

	private final Text playerKey;
	private final Text cheatKey;
	private final boolean suggestDuplicates;

	public PlayersAndCheatsArgument(Text playerKey, Text cheatKey) {
		this(playerKey, cheatKey, false);
	}

	public PlayersAndCheatsArgument(Text playerKey, Text cheatKey, boolean suggestDuplicates) {
		super(null);
		this.playerKey = playerKey;
		this.cheatKey = cheatKey;
		this.suggestDuplicates = suggestDuplicates;
	}

	@Override
	public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
		while (args.hasNext()) {
			String next = args.next();
			Optional<Player> maybePlayer = Sponge.getServer().getPlayer(next);
			if (maybePlayer.isPresent()) {
				context.putArg(playerKey, maybePlayer.get());
				continue;
			}

			Cheat maybeCheat = Cheat.fromString(next);
			if (maybeCheat != null) {
				context.putArg(cheatKey, maybeCheat);
				continue;
			}

			throw args.createError(Text.of("Not a player nor a cheat: " + next));
		}
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) {
		return null;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		Set<String> suggestions = Sponge.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
		suggestions.addAll(Cheat.getCheatKeys());

		if (!suggestDuplicates) {
			for (String arg : args.getRaw().split(" ")) {
				suggestions.remove(arg);
			}
		}

		return ImmutableList.copyOf(suggestions);
	}
}
