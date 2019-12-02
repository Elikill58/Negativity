package com.elikill58.negativity.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.TranslatedMessages;

public class LangCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, "only_player"));
			return false;
		}
		Player p = (Player) sender;
		if (arg.length == 0) {
			Messages.sendMessage(p, "lang.current", "%lang%",
					TranslatedMessages.getLang(p.getUniqueId()));
		} else {
			if (arg[0].equalsIgnoreCase("help")) {
				Messages.sendMessage(p, "lang.help");
			} else if (arg.length == 1) {
				String lang = "";
				for(String tempLang : TranslatedMessages.LANGS)
					if(tempLang.equalsIgnoreCase(arg[0]) || tempLang.contains(arg[0]))
						lang = tempLang;
				if(lang.equalsIgnoreCase("")) {
					Messages.sendMessage(p, "lang.invalid_lang", "%arg%", arg[0]);
					return false;
				}
				if (TranslatedMessages.activeTranslation) {
					SpigotNegativityPlayer.getNegativityPlayer(p).setLang(lang);
				} else {
					TranslatedMessages.DEFAULT_LANG = lang;
					SpigotNegativity.getInstance().getConfig().set("Translation.default", lang);
					SpigotNegativity.getInstance().saveConfig();
				}
				Messages.sendMessage(p, "lang.language_set");
			} else
				Messages.sendMessage(p, "lang.help");
		}
		return false;
	}

}
