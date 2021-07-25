package com.elikill58.negativity.velocity;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

public class VNegativityCommand implements SimpleCommand {

	@Override
	public void execute(Invocation inv) {
		CommandSource source = inv.source();
		String[] args = inv.arguments();
		if (source instanceof Player && !Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer((Player) source), Perm.ADMIN)) {
			source.sendMessage(VelocityMessages.getMessage((Player) source, "not_permission"));
			return;
		}

		if (args.length <= 0) {
			source.sendMessage(Component.text("You must use a subcommand"));
			return;
		}

		if (args[0].equalsIgnoreCase("admin")) {
			if (args.length > 1 && args[1].equalsIgnoreCase("updateMessages")) {
				MessagesUpdater.performUpdate("lang", (message, placeholders) -> VelocityMessages.sendMessage(source, message, placeholders));
			} else {
				source.sendMessage(Component.text("You must use a subcommand"));
			}
			return;
		} else if(args[0].equalsIgnoreCase("reload")) {
			Adapter.getAdapter().reload();
			VelocityMessages.sendMessage(source, "negativity.reload_done");
			return;
		}

		source.sendMessage(Component.text("Unknown subcommand"));
	}

	/*@Override
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
	}*/

	/*private static boolean hasAdminPermission(CommandSource source) {
		return !(source instanceof Player) || Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer((Player) source), Perm.ADMIN);
	}*/
}
