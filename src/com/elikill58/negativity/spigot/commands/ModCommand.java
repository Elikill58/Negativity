package com.elikill58.negativity.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class ModCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "mod"))
			Messages.sendMessage(p, "not_permission");
		else
			Inv.openModMenu(p);
		return false;
	}

}
