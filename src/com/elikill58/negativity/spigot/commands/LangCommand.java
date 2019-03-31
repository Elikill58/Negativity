package com.elikill58.negativity.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.TranslatedMessages;

public class LangCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, "only_player"));
			return false;
		}
		SpigotNegativityPlayer.getNegativityPlayer((Player) sender).getNegativityAccount().getLang();
		return false;
	}

}
