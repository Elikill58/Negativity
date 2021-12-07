package com.elikill58.negativity.spigot.listeners;

import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.others.CommandExecutionEvent;
import com.elikill58.negativity.api.events.others.TabExecutionEvent;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;

public class CommandsListeners implements CommandExecutor, TabCompleter, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		CommandExecutionEvent cmdEvent = new CommandExecutionEvent(cmd.getLabel().toLowerCase(Locale.ROOT), SpigotEntityManager.getExecutor(sender), arg, prefix);
		EventManager.callEvent(cmdEvent);
		return !cmdEvent.hasGoodResult();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		TabExecutionEvent tabEvent = new TabExecutionEvent(cmd.getLabel().toLowerCase(Locale.ROOT), SpigotEntityManager.getExecutor(sender), arg, arg[arg.length - 1].toLowerCase(Locale.ROOT));
		EventManager.callEvent(tabEvent);
		return tabEvent.getTabContent();
	}
	
	@EventHandler
	public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage().substring(1);
		String cmd = message.split(" ")[0];
		String[] arg = message.replace(cmd + " ", "").split(" ");
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		PlayerCommandPreProcessEvent event = new PlayerCommandPreProcessEvent(SpigotEntityManager.getPlayer(p), cmd, arg, prefix, false);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(true);
	}
}
