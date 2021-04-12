package com.elikill58.negativity.sponge.commands.child;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;

public class AlertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		Player p = (Player) src;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.disableShowingAlert = !np.disableShowingAlert;
		Messages.sendMessage(p, np.disableShowingAlert ? "negativity.see_no_longer_alert" : "negativity.see_alert");
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new AlertCommand())
				.permission("negativity.alert")
				.build();
	}
}
