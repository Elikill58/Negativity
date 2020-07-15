package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Messages;

public class KickCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length < 2) {
			Messages.sendMessage(sender, "kick.help");
			return false;
		}

		Player target = Bukkit.getPlayer(arg[0]);
		if (target == null) {
			for (Player onlinePlayer : Utils.getOnlinePlayers()) {
				if (arg[0].equalsIgnoreCase(onlinePlayer.getName())) {
					target = onlinePlayer;
					break;
				}
			}
		}
		if (target == null) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		StringJoiner stringJoiner = new StringJoiner(" ");
		for (int i = 1; i < arg.length; i++) {
			stringJoiner.add(arg[i]);
		}

		String reason = stringJoiner.toString();
		target.kickPlayer(Messages.getMessage(target, "kick.kicked", "%name%", target.getName(), "%reason%", reason));
		Messages.sendMessage(sender, "kick.well_kick", "%name%", target.getName(), "%reason%", reason);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		for (Player p : Utils.getOnlinePlayers()) {
			if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
				suggestions.add(p.getName());
			}
		}
		return suggestions;
	}
}
