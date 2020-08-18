package com.elikill58.negativity.velocity;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;

public class VNegativityCommand implements Command {

	@Override
	public void execute(CommandSource source, @NonNull String[] args) {
		if (source instanceof Player && !Perm.hasPerm(NegativityPlayer.getCached(((Player) source).getUniqueId()), Perm.ADMIN)) {
			source.sendMessage(VelocityMessages.getMessage((Player) source, "not_permission"));
			return;
		}

		if (args.length <= 0) {
			source.sendMessage(TextComponent.of("You must use a subcommand"));
			return;
		}

		if (args[0].equalsIgnoreCase("admin")) {
			if (args.length > 1 && args[1].equalsIgnoreCase("updateMessages")) {
				MessagesUpdater.performUpdate("lang", (message, placeholders) -> VelocityMessages.sendMessage(source, message, placeholders));
			} else {
				source.sendMessage(TextComponent.of("You must use a subcommand"));
			}
			return;
		}

		source.sendMessage(TextComponent.of("Unknown subcommand"));
	}

	@Override
	public List<String> suggest(CommandSource source, @NonNull String[] args) {
		if (args.length == 1) {
			if (hasAdminPermission(source)) {
				return Collections.singletonList("admin");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
			if (hasAdminPermission(source)) {
				return Collections.singletonList("updateMessages");
			}
		}
		return Collections.emptyList();
	}

	private static boolean hasAdminPermission(CommandSource source) {
		return !(source instanceof Player) || Perm.hasPerm(NegativityPlayer.getCached(((Player) source).getUniqueId()), Perm.ADMIN);
	}
}
