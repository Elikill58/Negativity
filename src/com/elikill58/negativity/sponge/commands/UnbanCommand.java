package com.elikill58.negativity.sponge.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.permissions.Perm;

public class UnbanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src instanceof Player) {
			Player playerSource = (Player) src;
			if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(playerSource), "unban")) {
				throw new CommandException(Messages.getMessage(playerSource, "not_permission"));
			}
		}

		User target = args.requireOne("target");
		Optional<Player> maybeTargetPlayer = target.getPlayer();
		SpongeNegativityPlayer targetNPlayer;
		if (maybeTargetPlayer.isPresent()) {
			targetNPlayer = SpongeNegativityPlayer.getNegativityPlayer(maybeTargetPlayer.get());
		} else {
			targetNPlayer = SpongeNegativityPlayer.getNegativityPlayer(target.getUniqueId());
		}

		List<BanRequest> banRequests = targetNPlayer.getBanRequest();
		for (BanRequest banRequest : banRequests) {
			banRequest.unban();
		}

		Messages.sendMessage(src, "unban.well_unban", "%name%", target.getName());
		return CommandResult.successCount(banRequests.size());
	}
}
