package com.elikill58.negativity.sponge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanCommand implements CommandCallable {

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
		return Text.of("/ban <player> <def | time> <reason>");
	}

	@Override
	public CommandResult process(CommandSource sender, String args) throws CommandException {
		if (!(sender instanceof Player))
			return CommandResult.empty();
		Player p = (Player) sender;
		if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "ban")) {
			Messages.sendMessage(p, "not_permission");
			return CommandResult.empty();
		}
		String[] arg = args.split(" ");
		if(arg.length <= 3) {
			Messages.sendMessageList(p, "ban.help");
			return CommandResult.empty();
		}
		Optional<Player> optionalCible = Sponge.getServer().getPlayer(arg[0]);
		if (!optionalCible.isPresent()) {
			Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			return CommandResult.empty();
		}
		Player cible = optionalCible.get();
		if(!UniversalUtils.isInteger(arg[1]) && !UniversalUtils.isBoolean(arg[1])) {
			Messages.sendMessageList(p, "ban.help");
			return CommandResult.empty();
		}
		
		int time = 0;
		boolean def = false;
		if(UniversalUtils.isBoolean(arg[1]))
			def = UniversalUtils.getFromBoolean(arg[1]);
		else time = Integer.parseInt(arg[1]);
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		String reason = "";
		for(String s : arg) {
			if(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
				continue;
			if(reason.equalsIgnoreCase(""))
				reason = s;
			else reason += s;
		}
		new BanRequest(np, reason, time, def, BanType.MOD, getFromReason(reason), p.getName()).execute();
		Messages.sendMessage(p, "ban.well_ban", "%name%", cible.getName(), "%reason%", reason);
		return CommandResult.empty();
	}
	
	private String getFromReason(String line) {
		for(String s : line.split(" "))
			for(Cheat c : Cheat.values())
				if(c.getName().equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s))
					return c.getName();
		return "mod";
	}
	@Override
	public boolean testPermission(CommandSource source) {
		return false;
	}

}
