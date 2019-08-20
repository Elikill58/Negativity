package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanRequest;

public class UnbanCommand implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			if(arg.length == 0) {
				Messages.sendMessage(sender, "unban.help");
				return false;
			}
			OfflinePlayer cible = Bukkit.getOfflinePlayer(arg[0]);
			if (cible == null) {
				for(OfflinePlayer offline : Bukkit.getOfflinePlayers())
					if(arg[0].equalsIgnoreCase(offline.getName()))
						cible = offline;
			}
			if (cible == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			SpigotNegativityPlayer npCible = SpigotNegativityPlayer.getNegativityPlayer(cible);
			List<BanRequest> brList =  npCible.getAccount().getBanRequest();
			if(brList.size() == 0) {
				if(cible.isOnline() || Ban.canConnect(npCible.getAccount()))
					Messages.sendMessage(sender, "unban.not_banned", "%name%", cible.getName());
				else
					Messages.sendMessage(sender, "unban.not_exact", "%arg%", arg[0]);
				return false;
			} else
				for(BanRequest br : brList)
					br.unban();
			Messages.sendMessage(sender, "unban.well_unban", "%name%", cible.getName());
			return true;
		}
		Player p = (Player) sender;
		if(arg.length == 0) {
			Messages.sendMessage(p, "unban.help");
			return false;
		}
		OfflinePlayer cible = Bukkit.getOfflinePlayer(arg[0]);
		if (cible == null) {
			for(OfflinePlayer offline : Bukkit.getOfflinePlayers())
				if(arg[0].equalsIgnoreCase(offline.getName()))
					cible = offline;
		}
		if (cible == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}
		SpigotNegativityPlayer npCible = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<BanRequest> brList =  npCible.getAccount().getBanRequest();
		if(brList.size() == 0) {
			if(cible.isOnline() || Ban.canConnect(npCible.getAccount()))
				Messages.sendMessage(p, "unban.not_banned", "%name%", cible.getName());
			else
				Messages.sendMessage(p, "unban.not_exact", "%arg%", arg[0]);
			return false;
		} else
			for(BanRequest br : brList)
				br.unban();
		Messages.sendMessage(p, "unban.well_unban", "%name%", cible.getName());
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {

		List<String> list = new ArrayList<String>();

		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase();
		if (arg.length == 1 || (arg.length == 2 && arg[1].equalsIgnoreCase(prefix))) {
			for (Player p : Utils.getOnlinePlayers())
				if (prefix.isEmpty() || p.getName().startsWith(prefix))
					list.add(p.getName());
		} else if(arg.length == 2 && arg[1].equalsIgnoreCase(prefix)) {
			if("def".startsWith(prefix))
				list.add("def");
		}
		return list;
	}
}
