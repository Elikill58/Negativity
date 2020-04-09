package com.elikill58.negativity.sponge.commands.child;

import java.util.List;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;

public class AlertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		List<PlayerCheatEvent.Alert> alerts = SpongeNegativity.ALERTS;
		if (alerts.isEmpty()) {
			Messages.sendMessage(src, "negativity.no_alerts");
			return CommandResult.empty();
		}

		for (int i = alerts.size() - 1; i >= 0; i--) {
			PlayerCheatEvent.Alert alert = alerts.get(i);
			src.sendMessage(SpongeNegativity.createAlertText(alert.getTargetEntity(), alert.getCheat(), alert.getHoverProof(),
					alert.getPing(), 0, "negativity.alert", alert.getReliability(), src));
		}
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new AlertCommand())
				.permission("negativity.alert")
				.build();
	}
}
