package com.elikill58.negativity.sponge.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.permissions.Perm;

public class UnbanCommand implements CommandCallable {

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public List<String> getSuggestions(CommandSource arg0, String arg1, Location<World> arg2) throws CommandException {
		return new ArrayList<>();
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of("/unban <player>");
	}

	@Override
	public CommandResult process(CommandSource sender, String arguments) throws CommandException {
		if (!(sender instanceof Player))
			return CommandResult.empty();
		Player p = (Player) sender;
		if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "unban")) {
			Messages.sendMessage(p, "not_permission");
			return CommandResult.empty();
		}
		String[] arg = arguments.split(" ");
		if(arg.length == 0) {
			Messages.sendMessageList(p, "unban.help");
			return CommandResult.empty();
		}
		Optional<User> optCible = Sponge.getServiceManager().provide(UserStorageService.class).get().get(arg[0]);
		if (!optCible.isPresent() || Sponge.getServer().getPlayer(arg[0]).isPresent()) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return CommandResult.empty();
		}
		UUID cible = optCible.get().getUniqueId();
		Iterator<BanRequest> br = SpongeNegativityPlayer.getNegativityPlayer(cible).getBanRequest().iterator();
		while(br.hasNext()) {
			br.next().unban();
		}
		Messages.sendMessage(p, "unban.well_unban", "%name%", optCible.get().getName());
		return CommandResult.empty();
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return false;
	}

}
