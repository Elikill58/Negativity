package com.elikill58.negativity.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class LangCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.getNegativityAccount().getLang();
		return false;
	}

}
