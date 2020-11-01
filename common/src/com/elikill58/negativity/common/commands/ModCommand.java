package com.elikill58.negativity.common.commands;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModCommand implements CommandListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.MOD)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if(!(sender instanceof Player)) {
			Messages.sendMessage(sender, "only_player");
			return false;
		}
		InventoryManager.open(NegativityInventory.MOD, (Player) sender);
		return false;
	}
}
