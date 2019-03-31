package com.elikill58.negativity.sponge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class NegativityCommand implements CommandCallable, CommandExecutor {

	@Override
	public CommandResult process(CommandSource source, String message) throws CommandException {
		if (!(source instanceof Player))
			return CommandResult.empty();
		String[] arg = message.split(" ");
		Player p = (Player) source;
		if (arg.length == 0)
			Messages.sendMessageList(p, "negativity.verif.help");
		else {
			if (arg[0].equalsIgnoreCase("verif")) {
				if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "verif")) {
					Messages.sendMessage(p, "not_permission");
					return CommandResult.empty();
				}
				if (arg.length == 1)
					Messages.sendMessage(p, "not_forget_player");
				else {
					Optional<Player> optionalCible = Sponge.getServer().getPlayer(arg[1]);
					if (!optionalCible.isPresent()) {
						Messages.sendMessage(p, "invalid_player", "%arg%", arg[1]);
						return CommandResult.empty();
					}
					Player cible = optionalCible.get();
					ArrayList<Cheat> actived = new ArrayList<>();
					if (arg.length > 2)
						for (String s : arg)
							if (!(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
									&& Cheat.getCheatFromString(s).isPresent())
								actived.add(Cheat.getCheatFromString(s).get());
					if (actived.size() == 0)
						actived.add(Cheat.ALL);
					SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
					for (Cheat c : actived)
						np.startAnalyze(c);
					if (actived.contains(Cheat.ALL)) {
						np.startAllAnalyze();
						Messages.sendMessage(p, "negativity.verif.start_all", "%name%", cible.getName());
					} else {
						String cheat = "";
						boolean isStart = true;
						for (Cheat c : actived)
							if (isStart) {
								cheat = c.getName();
								isStart = false;
							} else
								cheat = cheat + ", " + c.getName();
						Messages.sendMessage(p, "negativity.verif.start", "%name%", cible.getName(), "%cheat%", cheat);
					}
				}
			} else if (arg[0].equalsIgnoreCase("reload")) {
				p.sendMessage(Text.builder().color(TextColors.RED).append(Text.of("This command is not supported now !")).build());
				Adapter.getAdapter().reload();
			} else if (Sponge.getServer().getPlayer(arg[0]).isPresent()) {
				Player cible = Sponge.getServer().getPlayer(arg[0]).get();
				if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "verif")) {
					Messages.sendMessage(p, "not_permission");
					return CommandResult.empty();
				}
				Inv.CHECKING.put(p, cible);
				Inv.openCheckMenu(p, cible);
			} else
				Messages.sendMessageList(p, "negativity.verif.help");
		}
		return CommandResult.empty();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String message, @Nullable Location<World> targetPosition)
			throws CommandException {
		List<String> tab = new ArrayList<>();
		String[] arg = message.split(" ");
		String prefix = arg.length == 0 ? " " : arg[arg.length - 1].toLowerCase();
		if (arg.length == 0) {
			for (Player p : Utils.getOnlinePlayers())
				if (p.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty())
					tab.add(p.getName());
			if ("verif".startsWith(prefix) || prefix.isEmpty())
				tab.add("verif");
		} else if (arg.length == 1 && arg[0].equalsIgnoreCase(prefix)) {
			for (Player p : Utils.getOnlinePlayers())
				if (p.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty())
					tab.add(p.getName());
			if ("verif".startsWith(prefix.toLowerCase()) || prefix.isEmpty())
				tab.add("verif");
		} else {
			if (arg[0].equalsIgnoreCase("verif") && arg.length > 2) {
				if (Sponge.getServer().getPlayer(arg[1]).isPresent())
					for (Cheat c : Cheat.values())
						if ((c.getName().toLowerCase().startsWith(prefix.toLowerCase()) || prefix.isEmpty()))
							tab.add(c.getName());
			} else
				for (Player p : Utils.getOnlinePlayers())
					if (p.getName().startsWith(prefix) || prefix.isEmpty())
						tab.add(p.getName());
		}
		if (tab.size() == 0)
			for (Player p : Utils.getOnlinePlayers())
				if (p.getName().startsWith(prefix) || prefix.isEmpty())
					tab.add(p.getName());
		return tab;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.empty();
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of("");
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			return CommandResult.empty();
		String[] arg = args.toString().split(" ");
		Player p = (Player) src;
		if (arg.length == 0)
			Messages.sendMessageList(p, "negativity.verif.help");
		else {
			if (arg[0].equalsIgnoreCase("verif")) {
				if (!Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "verif")) {
					Messages.sendMessage(p, "not_permission");
					return CommandResult.empty();
				}
				if (arg.length == 1)
					Messages.sendMessage(p, "not_forget_player");
				else {
					Optional<Player> optionalCible = Sponge.getServer().getPlayer(arg[1]);
					if (!optionalCible.isPresent()) {
						Messages.sendMessage(p, "invalid_player", "%arg%", arg[1]);
						return CommandResult.empty();
					}
					Player cible = optionalCible.get();
					ArrayList<Cheat> actived = new ArrayList<>();
					if (arg.length > 2)
						for (String s : arg)
							if (!(s.equalsIgnoreCase(arg[0]) || s.equalsIgnoreCase(arg[1]))
									&& Cheat.getCheatFromString(s).isPresent())
								actived.add(Cheat.getCheatFromString(s).get());
					if (actived.size() == 0)
						actived.add(Cheat.ALL);
					SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
					for (Cheat c : actived)
						np.startAnalyze(c);
					if (actived.contains(Cheat.ALL)) {
						np.startAllAnalyze();
						Messages.sendMessage(p, "negativity.verif.start_all", "%name%", cible.getName());
					} else {
						String cheat = "";
						boolean isStart = true;
						for (Cheat c : actived)
							if (isStart) {
								cheat = c.getName();
								isStart = false;
							} else
								cheat = cheat + ", " + c.getName();
						Messages.sendMessage(p, "negativity.verif.start", "%name%", cible.getName(), "%cheat%", cheat);
					}
				}
			} else if (Sponge.getServer().getPlayer(arg[0]).isPresent()) {
				Player cible = Sponge.getServer().getPlayer(arg[0]).get();
				if (!p.hasPermission("negativity.verif") && !p.hasPermission("negativity.*")) {
					Messages.sendMessage(p, "not_permission");
					return CommandResult.empty();
				}
				Inv.CHECKING.put(p, cible);
				Inv.openCheckMenu(p, cible);
			} else
				Messages.sendMessageList(p, "negativity.verif.help");
		}
		return CommandResult.empty();
	}
}
