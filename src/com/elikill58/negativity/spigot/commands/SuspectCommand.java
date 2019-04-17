package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

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
		// Player p = (Player) sender;
		String msg = "";
		for (String s : arg)
			if (msg.equalsIgnoreCase(""))
				msg = s;
			else
				msg += s;
		String[] content = msg.split(" ");
		List<Player> suspected = new ArrayList<>();
		List<Cheat> cheats = new ArrayList<>();
		for (String s : content) {
			for (Cheat c : Cheat.values())
				for (String alias : c.getAliases())
					if (alias.equalsIgnoreCase(s) || alias.contains(s) || alias.startsWith(s))
						cheats.add(c);
			for (Player tempP : Utils.getOnlinePlayers()) {
				if (tempP.getName().equalsIgnoreCase(s) || tempP.getName().toLowerCase().startsWith(s)
						|| tempP.getName().contains(s))
					suspected.add(tempP);
				else if (tempP.getDisplayName() != null)
					if (tempP.getDisplayName().equalsIgnoreCase(s) || tempP.getDisplayName().toLowerCase().startsWith(s)
							|| tempP.getDisplayName().contains(s))
						suspected.add(tempP);
			}
		}
		String players = "";
		for (Player suspect : suspected) {
			if (players.equalsIgnoreCase(""))
				players = suspect.getName();
			else
				players += ", " + suspect.getName();
			SuspectManager.analyzeText(SpigotNegativityPlayer.getNegativityPlayer(suspect), cheats);
		}
		if (players.equalsIgnoreCase(""))
			players = Messages.getMessage((Player) sender, "none");
		SpigotNegativityPlayer.getNegativityPlayer((Player) sender).sendMessage("well_suspect", "%players%", players);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {

		List<String> list = new ArrayList<String>();

		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase();
		for (Player p : Utils.getOnlinePlayers())
			if (prefix.isEmpty() || p.getName().startsWith(prefix))
				list.add(p.getName());
		for (Cheat c : Cheat.values())
			if (prefix.isEmpty() || c.getName().startsWith(prefix))
				list.add(c.getName());
		return list;
	}
}
