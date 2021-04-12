package com.elikill58.negativity.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;

public class ModCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(!(sender instanceof Player)) {
			Messages.sendMessage(sender, "only_player");
			return false;
		}
		AbstractInventory.getInventory(InventoryType.MOD).ifPresent((inv) -> inv.openInventory((Player) sender));
		//ModInventory.openModMenu((Player) sender);
		return false;
	}
}
