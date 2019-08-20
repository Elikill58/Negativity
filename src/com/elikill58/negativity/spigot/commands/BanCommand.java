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
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			if(arg.length < 3) {
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
			if(arg[0].equalsIgnoreCase("help")) {
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
			Player cible = Bukkit.getPlayer(arg[0]);
			if (cible == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			boolean def = false;
			long time = 0;
			if(arg[1].equalsIgnoreCase("def")) {
				def = true;
			} else {
				String stringTime = "";
				for(String c : arg[1].split("")) {
					if(UniversalUtils.isInteger(c))
						stringTime += c;
					else {
						switch(c) {
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

			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
			String reason = "";
			for(String s : arg) {
				if(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
					continue;
				if(reason.equalsIgnoreCase(""))
					reason = s;
				else reason += s;
			}
			new BanRequest(np.getAccount(), reason, time, def, BanType.CONSOLE, getFromReason(reason), "admin", false).execute();
			Messages.sendMessage(sender, "ban.well_ban", "%name%", cible.getName(), "%reason%", reason);
			return false;
		}
		Player p = (Player) sender;
		if(arg.length < 3) {
			Messages.sendMessageList(p, "ban.help");
			return false;
		}
		if(arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(p, "ban.help");
			return false;
		}
		Player cible = Bukkit.getPlayer(arg[0]);
		if (cible == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}
		boolean def = false;
		long time = 0;
		if(arg[1].equalsIgnoreCase("def")) {
			def = true;
		} else {
			String stringTime = "";
			for(String c : arg[1].split("")) {
				if(UniversalUtils.isInteger(c))
					stringTime += c;
				else {
					switch(c) {
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
						Messages.sendMessageList(p, "ban.help");
						return false;
					}
					stringTime = "";
				}
			}
			time = time * 1000;
		}

		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		String reason = "";
		for(String s : arg) {
			if(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
				continue;
			if(reason.equalsIgnoreCase(""))
				reason = s;
			else reason += " " + s;
		}
		new BanRequest(np.getAccount(), reason, time, def, BanType.MOD, getFromReason(reason), p.getName(), false).execute();
		if (!sender.equals(cible))
			Messages.sendMessage(p, "ban.well_ban", "%name%", cible.getName(), "%reason%", reason);
		return false;
	}

	private String getFromReason(String line) {
		for(String s : line.split(" "))
			for(Cheat c : Cheat.values())
				if(c.getName().equalsIgnoreCase(s) || c.getKey().equalsIgnoreCase(s))
					return c.getName();
		return "mod";
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
		} else {
			for (Player p : Utils.getOnlinePlayers())
				if (prefix.isEmpty() || p.getName().startsWith(prefix))
					list.add(p.getName());
		}
		return list;
	}
}
