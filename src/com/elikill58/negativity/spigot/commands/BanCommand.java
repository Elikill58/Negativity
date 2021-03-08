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
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length < 3) {
			Messages.sendMessage(sender, "ban.help");
			return false;
		}

		if (arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessage(sender, "ban.help");
			return false;
		}

		Player target = Bukkit.getPlayer(arg[0]);
		if (target == null) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		long time = -1;
		if (!arg[1].equalsIgnoreCase("def")) {
			try {
				time = System.currentTimeMillis() + UniversalUtils.parseDuration(arg[1]) * 1000;
			} catch (IllegalArgumentException e) {
				String exMessage = e.getMessage();
				if (exMessage != null) {
					sender.sendMessage(exMessage);
				}
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
		}

		String cheatName = null;
		StringJoiner reasonJoiner = new StringJoiner(" ");
		for (int i = 2; i < arg.length; i++) {
			String element = arg[i];
			reasonJoiner.add(element);
			if (cheatName == null && Cheat.fromString(element) != null) {
				cheatName = element;
			}
		}

		String reason = reasonJoiner.toString();
		BanManager.executeBan(Ban.active(target.getUniqueId(), reason, sender.getName(), BanType.MOD, time, cheatName));
		// TODO check the result of execute ban
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
