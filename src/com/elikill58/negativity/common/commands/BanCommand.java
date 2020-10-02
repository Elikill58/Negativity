package com.elikill58.negativity.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.ChatUtils;

public class BanCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.BAN)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if (arg.length < 3) {
			Messages.sendMessageList(sender, "ban.help");
			return false;
		}

		if (arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(sender, "ban.help");
			return true;
		}

		Player target = Adapter.getAdapter().getPlayer(arg[0]);
		if (target == null) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		long time = -1;
		if (!arg[1].equalsIgnoreCase("def")) {
			try {
				time = System.currentTimeMillis() + ChatUtils.parseDurationToSeconds(arg[1]) * 1000;
			} catch (IllegalArgumentException e) {
				String exMessage = e.getMessage();
				if (exMessage != null) {
					sender.sendMessage(exMessage);
				}
				Messages.sendMessageList(sender, "ban.help");
				return false;
			}
		}

		String cheatName = null;
		StringJoiner reasonJoiner = new StringJoiner(" ");
		for (int i = 2; i < arg.length; i++) {
			String element = arg[i];
			reasonJoiner.add(element);
			if (cheatName == null && Cheat.fromString(element) != null) {
				cheatName = element;
			}
		}

		String reason = reasonJoiner.toString();
		BanResult ban = BanManager.executeBan(Ban.active(target.getUniqueId(), reason, sender.getName(), BanType.MOD, time, cheatName, target.getIP()));
		if(ban.isSuccess())
			Messages.sendMessage(sender, "ban.well_ban", "%name%", target.getName(), "%reason%", reason);
		else
			Messages.sendMessage(sender, "ban.fail_ban", "%name%", target.getName(), "%reason%", ban.getResultType().getName());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		if (arg.length == 2) {
			// /nban <player> |
			if ("def".startsWith(prefix)) {
				suggestions.add("def");
			}
		} else {
			// /nban | <duration> |...
			for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
				if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(p.getName());
				}
			}
		}
		return suggestions;
	}
}
