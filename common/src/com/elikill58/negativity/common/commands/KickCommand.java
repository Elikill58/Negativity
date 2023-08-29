package com.elikill58.negativity.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.webhooks.WebhookManager;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class KickCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if (!Perm.hasPerm(sender, Perm.MOD)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if (arg.length < 2) {
			Messages.sendMessage(sender, "kick.help");
			return false;
		}
		Adapter ada = Adapter.getAdapter();
		Player target = ada.getPlayer(arg[0]);
		if (target == null) {
			for (Player onlinePlayer : ada.getOnlinePlayers()) {
				if (arg[0].equalsIgnoreCase(onlinePlayer.getName())) {
					target = onlinePlayer;
					break;
				}
			}
		}
		if (target == null) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		StringJoiner stringJoiner = new StringJoiner(" ");
		for (int i = 1; i < arg.length; i++) {
			stringJoiner.add(arg[i]);
		}

		String reason = stringJoiner.toString();
		target.kick(Messages.getMessage(target, "kick.kicked", "%name%", target.getName(), "%reason%", reason));
		Messages.sendMessage(sender, "kick.well_kick", "%name%", target.getName(), "%reason%", reason);
		WebhookManager.send(new WebhookMessage(WebhookMessageType.KICK, target, sender.getName(), System.currentTimeMillis(), "%reason%", reason));
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
				suggestions.add(p.getName());
			}
		}
		return suggestions;
	}
}
