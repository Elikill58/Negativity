package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.TranslatedMessages;

public class SuspectCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, "only_player"));
			return false;
		}

		Set<Player> suspected = new HashSet<>();
		Set<Cheat> cheats = new HashSet<>();
		for (String segment : arg) {
			Cheat cheat = Cheat.fromString(segment);
			if (cheat != null) {
				cheats.add(cheat);
				continue;
			}

			String loweredSegment = segment.toLowerCase(Locale.ROOT);
			for (Player onlinePlayer : Utils.getOnlinePlayers()) {
				if (onlinePlayer.getName().toLowerCase(Locale.ROOT).contains(loweredSegment)
						|| (!onlinePlayer.getDisplayName().equals(onlinePlayer.getName())
						&& onlinePlayer.getDisplayName().toLowerCase(Locale.ROOT).contains(loweredSegment))) {
					// Either the name or the displayname matches
					suspected.add(onlinePlayer);
				}
			}
		}

		StringJoiner suspectsJoiner = new StringJoiner(", ");
		for (Player suspect : suspected) {
			suspectsJoiner.add(suspect.getName());
			SuspectManager.analyzeText(SpigotNegativityPlayer.getNegativityPlayer(suspect), cheats);
		}

		String suspectsList = suspectsJoiner.toString();
		if (suspectsList.isEmpty()) {
			suspectsList = Messages.getMessage((Player) sender, "none");
		}
		Messages.sendMessage(sender, "well_suspect", "%players%", suspectsList);
		return true;
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
		for (Cheat c : Cheat.values()) {
			if (prefix.isEmpty() || c.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
				suggestions.add(c.getName());
			}
		}
		return suggestions;
	}
}
