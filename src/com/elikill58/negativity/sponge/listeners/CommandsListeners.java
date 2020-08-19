package com.elikill58.negativity.sponge.listeners;

import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class CommandsListeners implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		EventManager.callEvent(new CommandExecutionEvent(cmd.getLabel().toLowerCase(), SpigotEntityManager.getExecutor(sender), arg, prefix));
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		TabExecutionEvent tabEvent = new TabExecutionEvent(cmd.getLabel().toLowerCase(), SpigotEntityManager.getExecutor(sender), arg, arg[arg.length - 1].toLowerCase(Locale.ROOT));
		EventManager.callEvent(tabEvent);
		return tabEvent.getTabContent();
	}
}
