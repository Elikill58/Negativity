package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.CheckMenuInventory;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class NegativityCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length == 0 || arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(sender, "negativity.verif.help");
			FileConfiguration conf = SpigotNegativity.getInstance().getConfig();
			if(conf.getBoolean("report_command"))
				Messages.sendMessage(sender, "report.report_usage");
			if(conf.getBoolean("ban_command"))
				Messages.sendMessageList(sender, "ban.help");
			if(conf.getBoolean("unban_command"))
				Messages.sendMessage(sender, "unban.help");
			if(conf.getBoolean("kick_command"))
				Messages.sendMessage(sender, "kick.help");
			if(conf.getBoolean("report_command"))
				Messages.sendMessage(sender, "report.report_usage");
			Messages.sendMessage(sender, "lang.help");
			return true;
		}

		if (arg[0].equalsIgnoreCase("verif")) {
			if (sender instanceof Player && !Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), "verif")) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}

			if (arg.length < 2) {
				Messages.sendMessage(sender, "not_forget_player");
				return true;
			}

			Player target = Bukkit.getPlayer(arg[1]);
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}

			SpigotNegativityPlayer nTarget = SpigotNegativityPlayer.getNegativityPlayer(target);
			if (arg.length == 2) {
				nTarget.startAllAnalyze();
				Messages.sendMessage(sender, "negativity.verif.start_all", "%name%", target.getName());
			} else {
				StringJoiner cheatNamesJoiner = new StringJoiner(", ");
				for (int i = 2; i < arg.length; i++) {
					Cheat cheat = Cheat.fromString(arg[i]);
					if (cheat != null) {
						nTarget.startAnalyze(cheat);
						cheatNamesJoiner.add(cheat.getName());
					}
				}

				String cheatsList = cheatNamesJoiner.toString();
				if (cheatsList.isEmpty()) {
					Messages.sendMessage(sender, "negativity.verif.start_none");
				} else {
					Messages.sendMessage(sender, "negativity.verif.start", "%name%", target.getName(), "%cheat%", cheatsList);
				}
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("alert")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}

			if (SpigotNegativity.alerts.isEmpty()) {
				Messages.sendMessage(sender, "negativity.no_alerts");
				return true;
			}

			for (int i = SpigotNegativity.alerts.size() - 1; i >= 0; i--) {
				PlayerCheatAlertEvent alert = SpigotNegativity.alerts.get(i);
				Player playerSender = (Player) sender;
				new ClickableText().addRunnableHoverEvent(
						Messages.getMessage(playerSender, "negativity.alert",
								"%name%", alert.getPlayer().getName(),
								"%cheat%", alert.getCheat().getName(),
								"%reliability%", String.valueOf(alert.getReliability())),
						Messages.getMessage(playerSender, "negativity.alert_hover",
								"%reliability%", String.valueOf(alert.getReliability()),
								"%ping%", String.valueOf(alert.getPing())) + (alert.getHoverProof().isEmpty() ? "" : "\n" + alert.getHoverProof()),
						"/negativity " + alert.getPlayer().getName())
						.sendToPlayer(playerSender);
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("reload")) {
			Adapter.getAdapter().reload();
			Messages.sendMessage(sender, "negativity.reload_done");
			return true;
		}

		Player targetPlayer = Bukkit.getPlayer(arg[0]);
		if (targetPlayer != null) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return false;
			}

			Player playerSender = (Player) sender;
			if (!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(playerSender), "verif")) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}

			Inv.CHECKING.put(playerSender, targetPlayer);
			CheckMenuInventory.openCheckMenu(playerSender, targetPlayer);
			return true;
		}

		Messages.sendMessageList(sender, "negativity.verif.help");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		if (arg.length == 1) {
			// /negativity |
			for (Player p : Utils.getOnlinePlayers()) {
				if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
					suggestions.add(p.getName());
				}
			}
			if ("verif".startsWith(prefix))
				suggestions.add("verif");
			if ("reload".startsWith(prefix))
				suggestions.add("reload");
			if ("alert".startsWith(prefix))
				suggestions.add("alert");
		} else {
			if (arg[0].equalsIgnoreCase("verif")) {
				if (arg.length == 2) {
					// /negativity verif |
					for (Player p : Utils.getOnlinePlayers()) {
						if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
							suggestions.add(p.getName());
						}
					}
				} else if (Bukkit.getPlayer(arg[1]) != null) {
					// /negativity verif <target> |
					for (Cheat c : Cheat.values()) {
						if (c.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
							suggestions.add(c.getName());
						}
					}
				}
			}
		}
		return suggestions;
	}
}
