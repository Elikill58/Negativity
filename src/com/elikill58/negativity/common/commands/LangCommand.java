package com.elikill58.negativity.common.commands;

import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.permissions.Perm;

public class LangCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.LANG)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
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
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		if (arg.length > 1) {
			return Collections.emptyList();
		}
		return TranslatedMessages.LANGS;
	}
}
