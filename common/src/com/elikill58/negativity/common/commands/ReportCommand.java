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
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.report.Report;
import com.elikill58.negativity.universal.webhooks.WebhookManager;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

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
		if (np.longs.get(CheatKeys.ALL, "report-cmd", 0l) > System.currentTimeMillis() && !Perm.hasPerm(np, Perm.REPORT_WAIT)) {
			Messages.sendMessage(p, "report_wait");
			return false;
		}

		if (arg.length < 2) {
			Messages.sendMessage(p, "report.help");
			return false;
		}

		Player target = Adapter.getAdapter().getPlayer(arg[0]);
		if (target == null) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		if (arg.length == 1) {
			InventoryManager.open(NegativityInventory.REPORT, p, target);
			return false;
		}

		StringJoiner reasonJoiner = new StringJoiner(" ");
		for (int i = 1; i < arg.length; i++) {
			reasonJoiner.add(arg[i]);
		}

		String reason = reasonJoiner.toString();
		report(p, target, reason);
		Messages.sendMessage(p, "report.well_report", "%name%", target.getName());
		return false;
	}
	
	public static void report(Player reporter, Player target, String reason) {
		String msg = Messages.getMessage("report.report_message", "%name%", target.getName(),
				"%report%", reporter.getName(), "%reason%", reason);
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			Negativity.sendReportMessage(reporter, reason, target.getName());
		} else {
			boolean alertSent = false;
			for (Player pl : Adapter.getAdapter().getOnlinePlayers())
				if (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(pl), Perm.SHOW_REPORT)) {
					alertSent = true;
					Adapter.getAdapter().sendMessageRunnableHover(pl, Messages.getMessage(pl, "report.report_message",
									"%name%", target.getName(), "%report%", reporter.getName(), "%reason%", reason),
							Messages.getMessage(pl, "report.report_message_hover",
									"%name%", target.getName()),
							"/negativity " + target.getName());
				}
			if (!alertSent) {
				REPORT_LAST.add(msg);
			}
		}
		NegativityAccount.get(target.getUniqueId()).getReports().add(new Report(reason, reporter.getUniqueId()));
		Adapter.getAdapter().getAccountManager().save(target.getUniqueId());

		NegativityPlayer.getNegativityPlayer(reporter).longs.set(CheatKeys.ALL, "report-cmd", System.currentTimeMillis()
				+ Adapter.getAdapter().getConfig().getInt("time_between_report"));
		
		WebhookManager.send(new WebhookMessage(WebhookMessageType.REPORT, target, reporter.getName(), System.currentTimeMillis(), "%reason%", reason));
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
