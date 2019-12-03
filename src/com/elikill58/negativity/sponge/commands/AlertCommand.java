package com.elikill58.negativity.sponge.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;

public class AlertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		Player player = ((Player) src);
		for (int i = SpongeNegativity.ALERTS.size() - 1; i >= 0; i--) {
			PlayerCheatEvent.Alert alert = SpongeNegativity.ALERTS.get(i);
			player.sendMessage(SpongeNegativity.createAlertText(alert.getTargetEntity(), alert.getCheat(), alert.getHoverProof(),
					alert.getPing(), 0, "negativity.alert", alert.getReliability(), player));
		}
		return CommandResult.success();
	}

	public static CommandCallable create() {
		CommandSpec command = CommandSpec.builder()
				.executor(new AlertCommand())
				.build();
		return new NegativityCmdWrapper(command, true, null);
	}
}
