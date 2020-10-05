package com.elikill58.negativity.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Report;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.permissions.Perm;

public class ReportCommand implements CommandListeners, TabListeners {

	public static final List<String> REPORT_LAST = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.REPORT)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if (!(sender instanceof Player)) {
			Messages.sendMessage(sender, "only_player");
			return false;
		}

		Player p = (Player) sender;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (np.TIME_REPORT > System.currentTimeMillis() && !Perm.hasPerm(np, Perm.REPORT_WAIT)) {
			Messages.sendMessage(p, "report_wait");
			return false;
		}

		if (arg.length < 2) {
			Messages.sendMessage(p, "report.report_usage");
			return false;
		}

		Player target = Adapter.getAdapter().getPlayer(arg[0]);
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
			Negativity.sendReportMessage(p, reason, target.getName());
		} else {
			boolean alertSent = false;
			for (Player pl : Adapter.getAdapter().getOnlinePlayers())
				if (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(pl), Perm.SHOW_REPORT)) {
					alertSent = true;
					Adapter.getAdapter().sendMessageRunnableHover(pl, Messages.getMessage(pl, "report.report_message",
									"%name%", target.getName(), "%report%", p.getName(), "%reason%", reason),
							Messages.getMessage(pl, "report.report_message_hover",
									"%name%", target.getName()),
							"/negativity " + target.getName());
				}
			if (!alertSent) {
				REPORT_LAST.add(msg);
			}
		}
		NegativityAccount.get(target.getUniqueId()).getReports().add(new Report(reason, p.getUniqueId()));
		NegativityPlayer.getNegativityPlayer(target).mustToBeSaved = true;

		Messages.sendMessage(p, "report.well_report", "%name%", target.getName());
		np.TIME_REPORT = System.currentTimeMillis()
				+ Adapter.getAdapter().getConfig().getInt("time_between_report");
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		if (arg.length >= 2) {
			for (Cheat c : Cheat.values()) {
				if (prefix.isEmpty() || c.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(c.getName());
				}
			}
		}

		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
				suggestions.add(p.getName());
			}
		}

		return suggestions;
	}
}
