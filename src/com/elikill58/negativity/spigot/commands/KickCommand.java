package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.utils.Utils;

public class KickCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			if(arg.length == 0) {
				Messages.sendMessage(sender, "kick.help");
				return false;
			}
			Player cible = Bukkit.getPlayer(arg[0]);
			if (cible == null) {
				for(Player offline : Bukkit.getOnlinePlayers())
					if(arg[0].equalsIgnoreCase(offline.getName()))
						cible = offline;
			}
			if (cible == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			String reason = "";
			for(String s : arg) {
				if(s.equalsIgnoreCase(arg[0]))
					continue;
				if(reason.equalsIgnoreCase(""))
					reason = s;
				else reason += " " + s;
			}
			cible.kickPlayer(Messages.getMessage(cible, "kick.kicked", "%name%", cible.getName(), "%reason%", reason));
			Messages.sendMessage(sender, "kick.well_kick", "%name%", cible.getName(), "%reason%", reason);
			return true;
		}
		Player p = (Player) sender;
		if(arg.length == 0) {
			Messages.sendMessage(p, "kick.help");
			return false;
		}
		Player cible = Bukkit.getPlayer(arg[0]);
		if (cible == null) {
			for(Player offline : Bukkit.getOnlinePlayers())
				if(arg[0].equalsIgnoreCase(offline.getName()))
					cible = offline;
		}
		if (cible == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}
		String reason = "";
		for(String s : arg) {
			if(s.equalsIgnoreCase(arg[0]))
				continue;
			if(reason.equalsIgnoreCase(""))
				reason = s;
			else reason += " " + s;
		}
		cible.kickPlayer(Messages.getMessage(cible, "kick.kicked", "%name%", cible.getName(), "%reason%", reason));
		Messages.sendMessage(sender, "kick.well_kick", "%name%", cible.getName(), "%reason%", reason);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> list = new ArrayList<String>();
		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase();
		for (Player p : Utils.getOnlinePlayers())
			if (prefix.isEmpty() || p.getName().startsWith(prefix))
				list.add(p.getName());
		return list;
	}
}
