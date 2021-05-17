package com.elikill58.negativity.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.ChatUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.BAN)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if(!BanManager.banActive) {
			Messages.sendMessage(sender, "ban.not_active");
			return false;
		}
		if(arg.length >= 1 && arg[0].equalsIgnoreCase("list")) {
			List<Ban> activeBan = BanManager.getProcessor().getAllBans();
			if(activeBan.isEmpty()) {
				Messages.sendMessage(sender, "ban.list.none");
				return false;
			}
			int linePerPage = 10;
			int start = 0, end = linePerPage;
			if(arg.length >= 2 && UniversalUtils.isInteger(arg[1])) {
				// selecting page
				int page = Integer.parseInt(arg[1]);
				start = page * linePerPage;
				end = (page + 1) * linePerPage;
			}
			if(end > activeBan.size())
				end = activeBan.size();
			Messages.sendMessage(sender, "ban.list.header", "%start%", start + 1, "%end%", end, "%max%", activeBan.size());
			for(int i = start; i < end; i++) {
				if(activeBan.size() <= i)
					return false;
				Ban ban = activeBan.get(i);
				Messages.sendMessage(sender, "ban.list.line", "%number%", i + 1, "%name%", Adapter.getAdapter().getOfflinePlayer(ban.getPlayerId()).getName(), "%by%", ban.getBannedBy(), "%reason%", ban.getReason());
			}
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

		OfflinePlayer target = Adapter.getAdapter().getOfflinePlayer(arg[0]);
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
		BanResult ban = BanManager.executeBan(Ban.active(target.getUniqueId(), reason, sender.getName(), BanType.MOD, time, cheatName, (target instanceof Player ? ((Player) target).getIP() : null)));
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
