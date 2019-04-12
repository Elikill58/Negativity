package com.elikill58.negativity.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			if(arg.length < 3) {
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
			Player cible = Bukkit.getPlayer(arg[0]);
			if (cible == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			if(!UniversalUtils.isInteger(arg[1]) && !UniversalUtils.isBoolean(arg[1])) {
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
			
			int time = 0;
			boolean def = false;
			if(UniversalUtils.isBoolean(arg[1]))
				def = UniversalUtils.getFromBoolean(arg[1]);
			else time = Integer.parseInt(arg[1]);
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
			String reason = "";
			for(String s : arg) {
				if(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
					continue;
				if(reason.equalsIgnoreCase(""))
					reason = s;
				else reason += s;
			}
			new BanRequest(np, reason, time, def, BanType.CONSOLE, getFromReason(reason), "admin", false).execute();
			Messages.sendMessage(sender, "ban.well_ban", "%name%", cible.getName(), "%reason%", reason);
			return false;
		}
		Player p = (Player) sender;
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "ban")) {
			Messages.sendMessage(p, "not_permission");
			return false;
		}
		if(arg.length < 3) {
			Messages.sendMessageList(p, "ban.help");
			return false;
		}
		Player cible = Bukkit.getPlayer(arg[0]);
		if (cible == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}
		if(!UniversalUtils.isInteger(arg[1]) && !UniversalUtils.isBoolean(arg[1])) {
			Messages.sendMessageList(p, "ban.help");
			return false;
		}
		
		int time = 0;
		boolean def = false;
		if(UniversalUtils.isBoolean(arg[1]))
			def = UniversalUtils.getFromBoolean(arg[1]);
		else time = Integer.parseInt(arg[1]);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		String reason = "";
		for(String s : arg) {
			if(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
				continue;
			if(reason.equalsIgnoreCase(""))
				reason = s;
			else reason += " " + s;
		}
		new BanRequest(np, reason, time, def, BanType.MOD, getFromReason(reason), p.getName(), false).execute();
		np.kickPlayer(reason, String.valueOf(time), p.getName(), def);
		Messages.sendMessage(p, "ban.well_ban", "%name%", cible.getName(), "%reason%", reason);
		return false;
	}
	
	private String getFromReason(String line) {
		for(String s : line.split(" "))
			for(Cheat c : Cheat.values())
				if(c.getName().equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s))
					return c.getName();
		return "mod";
	}
}
