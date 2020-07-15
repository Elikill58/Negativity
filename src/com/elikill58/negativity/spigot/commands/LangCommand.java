package com.elikill58.negativity.spigot.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.TranslatedMessages;

public class LangCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			Messages.sendMessage(sender, "only_player");
			return false;
		}

		Player p = (Player) sender;
		if (arg.length == 0) {
			Messages.sendMessage(p, "lang.current", "%lang%", TranslatedMessages.getLang(p.getUniqueId()));
			return true;
		}

		if (arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessage(p, "lang.help");
		} else if (arg.length == 1) {
			String lang = null;
			for (String tempLang : TranslatedMessages.LANGS) {
				if (tempLang.equalsIgnoreCase(arg[0]) || tempLang.contains(arg[0]))
					lang = tempLang;
			}

			if (lang == null) {
				Messages.sendMessage(p, "lang.invalid_lang", "%arg%", arg[0]);
				return false;
			}

			if (!TranslatedMessages.activeTranslation) {
				Messages.sendMessage(p, "lang.translation_disabled");
				return true;
			}

			NegativityPlayer.getCached(p.getUniqueId()).getAccount().setLang(lang);
			Messages.sendMessage(p, "lang.language_set");
		} else {
			Messages.sendMessage(p, "lang.help");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length > 1) {
			return Collections.emptyList();
		}
		return TranslatedMessages.LANGS;
	}
}
