package com.elikill58.negativity.spigot.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
		if (!(sender instanceof Player)) {
			if (arg.length == 0)
				Messages.sendMessageList(sender, "negativity.verif.help");
			else {
				if (arg[0].startsWith("verif")) {
					if (arg.length == 1)
						Messages.sendMessage(sender, "not_forget_player");
					else {
						Player cible = Bukkit.getPlayer(arg[1]);
						if (cible == null) {
							Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
							return false;
						}
						ArrayList<Cheat> actived = new ArrayList<>();
						if (arg.length > 2)
							for (String s : arg)
								if (!(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
										&& Cheat.getCheatFromString(s).isPresent())
									actived.add(Cheat.getCheatFromString(s).get());
						if (actived.size() == 0)
							actived.add(Cheat.ALL);
						SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
						for (Cheat c : actived)
							np.startAnalyze(c);
						if (actived.contains(Cheat.ALL)) {
							np.startAllAnalyze();
							Messages.sendMessage(sender, "negativity.verif.start_all", "%name%", cible.getName());
						} else {
							String cheat = "";
							boolean isStart = true;
							for (Cheat c : actived)
								if (isStart) {
									cheat = c.getName();
									isStart = false;
								} else
									cheat = cheat + ", " + c.getName();
							Messages.sendMessage(sender, "negativity.verif.start", "%name%", cible.getName(), "%cheat%", cheat);
						}
					}
				} else if (arg[0].startsWith("alert")) {
					Messages.sendMessage(sender, "only_player");
				} else if (arg[0].equalsIgnoreCase("reload")) {
					Adapter.getAdapter().reload();
				} else if (Bukkit.getPlayer(arg[0]) != null) {
					Messages.sendMessage(sender, "only_player");
				} else
					Messages.sendMessageList(sender, "negativity.verif.help");
			}
			return false;
		}
		Player p = (Player) sender;
		if (arg.length == 0)
			Messages.sendMessageList(p, "negativity.verif.help");
		else {
			if (arg[0].startsWith("verif")) {
				if (!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "verif")) {
					Messages.sendMessage(p, "not_permission");
					return false;
				}
				if (arg.length == 1)
					Messages.sendMessage(p, "not_forget_player");
				else {
					Player cible = Bukkit.getPlayer(arg[1]);
					if (cible == null) {
						Messages.sendMessage(p, "invalid_player", "%arg%", arg[1]);
						return false;
					}
					ArrayList<Cheat> actived = new ArrayList<>();
					if (arg.length > 2)
						for (String s : arg)
							if (!(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
									&& Cheat.getCheatFromString(s).isPresent())
								actived.add(Cheat.getCheatFromString(s).get());
					if (actived.size() == 0)
						actived.add(Cheat.ALL);
					SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
					for (Cheat c : actived)
						np.startAnalyze(c);
					if (actived.contains(Cheat.ALL)) {
						np.startAllAnalyze();
						Messages.sendMessage(p, "negativity.verif.start_all", "%name%", cible.getName());
					} else {
						String cheat = "";
						boolean isStart = true;
						for (Cheat c : actived)
							if (isStart) {
								cheat = c.getName();
								isStart = false;
							} else
								cheat = cheat + ", " + c.getName();
						Messages.sendMessage(p, "negativity.verif.start", "%name%", cible.getName(), "%cheat%", cheat);
					}
				}
			} else if (arg[0].startsWith("alert")) {
				int alertSize = SpigotNegativity.alerts.size();
				for(int i = alertSize - 1; i < alertSize - 10; i--) {
					PlayerCheatAlertEvent alert = SpigotNegativity.alerts.get(i);
					new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(p, "negativity.alert", "%name%", alert.getPlayer().getName(), "%cheat%", alert.getCheat().getName(),
									"%reliability%", String.valueOf(alert.getReliability())),
							Messages.getMessage(p, "negativity.alert_hover", "%reliability%",
									String.valueOf(alert.getReliability()), "%ping%", String.valueOf(alert.getPing()))
									+ (alert.getHoverProof().equalsIgnoreCase("") ? "" : "\n" + alert.getHoverProof()),
							"/negativity " + alert.getPlayer().getName()).sendToPlayer(p);
				}
			} else if (arg[0].equalsIgnoreCase("reload")) {
				Adapter.getAdapter().reload();
			} else if (Bukkit.getPlayer(arg[0]) != null) {
				Player cible = Bukkit.getPlayer(arg[0]);
				if (!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "verif")) {
					Messages.sendMessage(p, "not_permission");
					return false;
				}
				Inv.CHECKING.put(p, cible);
				CheckMenuInventory.openCheckMenu(p, cible);
			} else
				Messages.sendMessageList(p, "negativity.verif.help");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> tab = new ArrayList<>();
		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase();
		if (arg.length == 0) {
			for (Player p : Utils.getOnlinePlayers())
				if (p.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty())
					tab.add(p.getName());
			if ("verif".startsWith(prefix) || prefix.isEmpty())
				tab.add("verif");
			if ("reload".startsWith(prefix) || prefix.isEmpty())
				tab.add("reload");
			if ("alert".startsWith(prefix) || prefix.isEmpty())
				tab.add("alert");
		} else if(arg.length == 1 && arg[0].equalsIgnoreCase(prefix)){
			for (Player p : Utils.getOnlinePlayers())
				if (p.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty())
					tab.add(p.getName());
			if ("verif".startsWith(prefix.toLowerCase()) || prefix.isEmpty())
				tab.add("verif");
			if ("reload".startsWith(prefix) || prefix.isEmpty())
				tab.add("reload");
			if ("alert".startsWith(prefix) || prefix.isEmpty())
				tab.add("alert");
		} else {
			if (arg[0].equalsIgnoreCase("verif") && arg.length > 2) {
				if (Bukkit.getPlayer(arg[1]) != null)
					for (Cheat c : Cheat.values())
						if (c.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty())
							tab.add(c.getName());
			} else
				for (Player p : Utils.getOnlinePlayers())
					if (p.getName().startsWith(prefix) || prefix.isEmpty())
						tab.add(p.getName());
		}
		return tab;
	}
}
