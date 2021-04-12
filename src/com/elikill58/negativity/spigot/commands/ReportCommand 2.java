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
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class ReportCommand implements CommandExecutor, TabCompleter {

	public static final List<String> REPORT_LAST = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player)) {
			Messages.sendMessage(sender, "only_player");
			return false;
		}

		Player p = (Player) sender;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (np.TIME_REPORT > System.currentTimeMillis() && !Perm.hasPerm(np, Perm.REPORT_WAIT)) {
			Messages.sendMessage(p, "report_wait");
			return false;
		}

		if (arg.length < 2) {
			Messages.sendMessage(p, "report.report_usage");
			return false;
		}

		Player target = Bukkit.getPlayer(arg[0]);
		if (target == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		StringJoiner reasonJoiner = new StringJoiner(" ");
		for (int i = 1; i < arg.length; i++) {
			reasonJoiner.add(arg[i]);
		}

		String reason = reasonJoiner.toString();
		String msg = Messages.getMessage("report.report_message", "%name%", target.getName(),
				"%report%", p.getName(), "%reason%", reason);
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			SpigotNegativity.sendReportMessage(p, reason, target.getName());
		} else {
			boolean alertSent = false;
			for (Player pl : Utils.getOnlinePlayers())
				if (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), Perm.SHOW_REPORT)) {
					alertSent = true;
					new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(pl, "report.report_message",
									"%name%", target.getName(), "%report%", p.getName(), "%reason%", reason),
							Messages.getMessage(pl, "report.report_message_hover",
									"%name%", target.getName()),
							"/negativity " + target.getName())
							.sendToPlayer(pl);
				}
			if (!alertSent) {
				REPORT_LAST.add(msg);
			}
		}

		Messages.sendMessage(p, "report.well_report", "%name%", target.getName());
		np.TIME_REPORT = System.currentTimeMillis()
				+ Adapter.getAdapter().getConfig().getInt("time_between_report");
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		if (arg.length >= 2) {
			for (Cheat c : Cheat.values()) {
				if (prefix.isEmpty() || c.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(c.getName());
				}
			}
		}

		for (Player p : Utils.getOnlinePlayers()) {
			if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
				suggestions.add(p.getName());
			}
		}

		return suggestions;
	}
}
