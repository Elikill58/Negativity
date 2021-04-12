package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;

public class UnbanCommand implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length == 0) {
			Messages.sendMessage(sender, "unban.help");
			return false;
		}

		OfflinePlayer cible = Bukkit.getOfflinePlayer(arg[0]);
		if (!cible.hasPlayedBefore()) {
			for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
				if (arg[0].equalsIgnoreCase(offline.getName())) {
					cible = offline;
				}
			}
		}
		if (!cible.hasPlayedBefore()) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		if (!BanManager.isBanned(cible.getUniqueId())) {
			Messages.sendMessage(sender, "unban.not_banned", "%name%", cible.getName());
			return false;
		}

		Ban revokedBan = BanManager.revokeBan(cible.getUniqueId());
		if (revokedBan != null) {
			Messages.sendMessage(sender, "unban.well_unban", "%name%", cible.getName());
			return true;
		} else {
			// Tell the sender the revocation failed
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		if (arg.length == 1) {
			// /nunban |
			for (Player p : Utils.getOnlinePlayers()) {
				if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(p.getName());
				}
			}
		}
		return suggestions;
	}
}
