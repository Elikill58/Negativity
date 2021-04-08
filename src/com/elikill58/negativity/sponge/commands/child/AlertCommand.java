package com.elikill58.negativity.sponge.commands.child;

import java.io.IOException;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityPlayerUpdateMessage;

public class AlertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		Player p = (Player) src;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.setShowAlert(!np.isShowAlert());
		Messages.sendMessage(p, np.isShowAlert() ? "negativity.see_alert" : "negativity.see_no_longer_alert");
		SpongeNegativity.channel.sendTo(p, (buffer) -> {
			try {
				buffer.writeBytes(NegativityMessagesManager.writeMessage(new NegativityPlayerUpdateMessage(np)));
			} catch (IOException ex) {
				SpongeNegativity.getInstance().getLogger().error("Could not write ProxyPingMessage.", ex);
			}
		});
		return CommandResult.success();
	}

	public static CommandCallable create() {
		return CommandSpec.builder()
				.executor(new AlertCommand())
				.permission("negativity.alert")
				.build();
	}
}
