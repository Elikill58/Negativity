package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.AbstractCheat;
import com.elikill58.negativity.universal.SuspectManager;

public class SuspectCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player))
			return false;
		//Player p = (Player) sender;
		String msg = "";
		for(String s : arg)
			if(msg.equalsIgnoreCase(""))
				msg = s;
			else msg += s;
		String[] content = msg.split(" ");
		List<Player> suspected = new ArrayList<>();
		List<AbstractCheat> cheats = new ArrayList<>();
		for(String s : content) {
			for(Cheat c : Cheat.values())
				for(String alias : c.getAliases())
					if(alias.equalsIgnoreCase(s) || alias.contains(s) || alias.startsWith(s))
						cheats.add(c);
			for(Player tempP : Utils.getOnlinePlayers()) {
				if(tempP.getName().equalsIgnoreCase(s) || tempP.getName().toLowerCase().startsWith(s) || tempP.getName().contains(s))
					suspected.add(tempP);
				else if(tempP.getDisplayName() != null)
					if(tempP.getDisplayName().equalsIgnoreCase(s) || tempP.getDisplayName().toLowerCase().startsWith(s) || tempP.getDisplayName().contains(s))
						suspected.add(tempP);
			}
		}
		for(Player suspect : suspected)
			SuspectManager.analyzeText(SpigotNegativityPlayer.getNegativityPlayer(suspect), cheats);
		return false;
	}

}
