package com.elikill58.negativity.bungee;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.translation.MessagesUpdater;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BNegativityCommand extends Command {

	public BNegativityCommand() {
		super("bnegativity");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer && !Perm.hasPerm(NegativityPlayer.getCached(((ProxiedPlayer) sender).getUniqueId()), Perm.ADMIN)) {
			sender.sendMessage(new TextComponent(Messages.getMessage(((ProxiedPlayer) sender).getUniqueId(), "not_permission")));
			return;
		}

		if (args.length <= 0) {
			sender.sendMessage(new TextComponent("You must use a subcommand"));
			return;
		}

		if (args[0].equalsIgnoreCase("admin")) {
			if (args.length > 1 && args[1].equalsIgnoreCase("updateMessages")) {
				MessagesUpdater.performUpdate("lang", (message, placeholders) -> sender.sendMessage(new ComponentBuilder(Messages.getMessage(message, (Object[]) placeholders)).create()));
			} else {
				sender.sendMessage(new TextComponent("You must use a subcommand"));
			}
			return;
		}

		sender.sendMessage(new TextComponent("Unknown subcommand"));
	}

	public static class TabCompleter implements Listener {

		@EventHandler
		public void complete(TabCompleteEvent event) {
			String[] parts = event.getCursor().split(" ");
			if (parts.length == 0) {
				return;
			}

			if (parts.length == 1 && !parts[0].equalsIgnoreCase("/bnegativity")) {
				if ("/bnegativity".startsWith(parts[0]) && hasAdminPermission(event.getSender())) {
					event.getSuggestions().add("/bnegativity");
				}
				return;
			}

			if (!event.getCursor().endsWith(" ") // We are not completing the next part
					|| !hasAdminPermission(event.getSender())) { // We only complete admin commands for now
				return;
			}

			if (parts.length == 1) {
				event.getSuggestions().add("admin");
			} else if (parts.length == 2 && parts[1].equalsIgnoreCase("admin")) {
				event.getSuggestions().add("updateMessages");
			}
		}

		private static boolean hasAdminPermission(Connection sender) {
			return !(sender instanceof ProxiedPlayer) || Perm.hasPerm(NegativityPlayer.getCached(((ProxiedPlayer) sender).getUniqueId()), Perm.ADMIN);
		}
	}
}
