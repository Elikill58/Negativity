package com.elikill58.negativity.spigot.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.permissions.Perm;

public class UnbanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			if(arg.length == 0) {
				Messages.sendMessageList(sender, "unban.help");
				return false;
			}
			@SuppressWarnings("deprecation")
			OfflinePlayer cible = Bukkit.getOfflinePlayer(arg[0]);
			if (cible == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
				return false;
			}
			List<BanRequest> brList =  SpigotNegativityPlayer.getNegativityPlayer(cible).getBanRequest();
			if(brList.size() == 0) {
				if(cible.isOnline())
					Messages.sendMessage(sender, "unban.not_banned", "%name%", cible.getName());
				else Messages.sendMessage(sender, "unban.not_exact", "%arg%", arg[0]);
			} else
				for(BanRequest br : brList)
					br.unban();
			Messages.sendMessage(sender, "unban.well_unban", "%name%", cible.getName());
			return true;
		}
		Player p = (Player) sender;
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "ban")) {
			Messages.sendMessage(p, "not_permission");
			return false;
		}
		if(arg.length == 0) {
			Messages.sendMessageList(p, "unban.help");
			return false;
		}
		@SuppressWarnings("deprecation")
		OfflinePlayer cible = Bukkit.getOfflinePlayer(arg[0]);
		if (cible == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}
		List<BanRequest> brList =  SpigotNegativityPlayer.getNegativityPlayer(cible).getBanRequest();
		if(brList.size() == 0) {
			if(cible.isOnline())
				Messages.sendMessage(p, "unban.not_banned", "%name%", cible.getName());
			else Messages.sendMessage(p, "unban.not_exact", "%arg%", arg[0]);
		} else
			for(BanRequest br : brList)
				br.unban();
		Messages.sendMessage(p, "unban.well_unban", "%name%", cible.getName());
		return false;
	}

}
