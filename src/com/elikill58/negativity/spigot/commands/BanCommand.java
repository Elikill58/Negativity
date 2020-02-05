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

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length < 3) {
			Messages.sendMessageList(sender, "ban.help");
			return false;
		}

		if (arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(sender, "ban.help");
			return false;
		}

		Player target = Bukkit.getPlayer(arg[0]);
		if (target == null) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		long time = -1;
		if (!arg[1].equalsIgnoreCase("def")) {
			String stringTime = "";
			for (String c : arg[1].split("")) {
				if (UniversalUtils.isInteger(c)) {
					stringTime += c;
				} else {
					switch (c) {
					case "s":
						time += Integer.parseInt(stringTime);
						break;
					case "m":
						time += Integer.parseInt(stringTime) * 60;
						break;
					case "h":
						time += Integer.parseInt(stringTime) * 3600;
						break;
					case "j":
					case "d":
						time += Integer.parseInt(stringTime) * 3600 * 24;
						break;
					case "mo":
						time += Integer.parseInt(stringTime) * 3600 * 24 * 30;
						break;
					case "y":
						time += Integer.parseInt(stringTime) * 3600 * 24 * 30 * 12;
						break;
					default:
						Messages.sendMessageList(sender, "ban.help");
						return false;
					}
					stringTime = "";
				}
			}
			time = time * 1000;
		}

		String reason = null;
		StringJoiner reasonJoiner = new StringJoiner(" ");
		for (int i = 2; i < arg.length; i++) {
			String element = arg[i];
			reasonJoiner.add(element);
			if (reason == null && Cheat.fromString(element) != null) {
				reason = element;
			}
		}
		BanManager.executeBan(new ActiveBan(target.getUniqueId(), reason, sender.getName(), BanType.MOD, System.currentTimeMillis() + time, reason));
		Messages.sendMessage(sender, "ban.well_ban", "%name%", target.getName(), "%reason%", reason);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		if (arg.length == 2) {
			// /nban <player> |
			if ("def".startsWith(prefix)) {
				suggestions.add("def");
			}
		} else {
			// /nban | <duration> |...
			for (Player p : Utils.getOnlinePlayers()) {
				if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(p.getName());
				}
			}
		}
		return suggestions;
	}
}
