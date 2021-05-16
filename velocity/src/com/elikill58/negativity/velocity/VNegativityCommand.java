package com.elikill58.negativity.velocity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

public class VNegativityCommand implements SimpleCommand {
	
	private static boolean hasAdminPermission(CommandSource source) {
		return !(source instanceof Player) || Perm.hasPerm(NegativityPlayer.getCached(((Player) source).getUniqueId()), Perm.ADMIN);
	}
	
	@Override
	public void execute(Invocation invocation) {
		CommandSource source = invocation.source();
		if (source instanceof Player && !Perm.hasPerm(NegativityPlayer.getCached(((Player) source).getUniqueId()), Perm.ADMIN)) {
			source.sendMessage(Identity.nil(), Component.text(Messages.getMessage(((Player) source).getUniqueId(), "not_permission")));
			return;
		}
		
		String[] args = invocation.arguments();
		if (args.length <= 0) {
			source.sendMessage(Component.text("You must use a subcommand"));
			return;
		}
		
		if (args[0].equalsIgnoreCase("admin")) {
			if (args.length > 1 && args[1].equalsIgnoreCase("updateMessages")) {
				MessagesUpdater.performUpdate("lang", (message, placeholders) -> source.sendMessage(Component.text(Messages.getMessage(message, (Object[]) placeholders))));
			} else {
				source.sendMessage(Component.text("You must use a subcommand"));
			}
			return;
		} else if (args[0].equalsIgnoreCase("reload")) {
			Adapter.getAdapter().reload();
			source.sendMessage(Component.text(Messages.getMessage("negativity.reload_done")));
			return;
		}
		
		source.sendMessage(Component.text("Unknown subcommand"));
	}
	
	@Override
	public List<String> suggest(Invocation invocation) {
		CommandSource source = invocation.source();
		String[] args = invocation.arguments();
		if (args.length < 2) {
			if (hasAdminPermission(source)) {
				return Arrays.asList("admin", "reload");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
			if (hasAdminPermission(source)) {
				return Collections.singletonList("updateMessages");
			}
		}
		return Collections.emptyList();
	}
}
