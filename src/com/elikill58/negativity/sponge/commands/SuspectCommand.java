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

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.SuspectManager;

public class SuspectCommand implements CommandCallable {

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
		return Text.of();
	}

	@Override
	public CommandResult process(CommandSource sender, String msg) throws CommandException {
        if (!(sender instanceof Player))
            return CommandResult.empty();
		//Player p = (Player) sender;
		String[] content = msg.split(" ");
		List<Player> suspected = new ArrayList<>();
		List<Cheat> cheats = new ArrayList<>();
		for(String s : content) {
			for(Cheat c : Cheat.values())
				for(String alias : c.getAliases())
					if(alias.equalsIgnoreCase(s) || alias.contains(s) || alias.startsWith(s))
						cheats.add(c);
			for(Player tempP : Sponge.getServer().getOnlinePlayers()) {
				if(tempP.getName().equalsIgnoreCase(s) || tempP.getName().toLowerCase().startsWith(s) || tempP.getName().contains(s))
					suspected.add(tempP);
			}
		}
		for(Player suspect : suspected)
			SuspectManager.analyzeText(SpongeNegativityPlayer.getNegativityPlayer(suspect), cheats);
		return CommandResult.empty();
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return false;
	}

}
