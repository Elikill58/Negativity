package com.elikill58.negativity.common.commands;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;

public class NegativityTpCommand implements Listeners {

	@EventListener
	public void onCommandPreProcess(PlayerCommandPreProcessEvent e) {
		Player p = e.getPlayer();
		if(!e.getCommand().equalsIgnoreCase("negativitytp"))
			return; // not my command -> ignore
		
		if (!Perm.hasPerm(p, Perm.SHOW_ALERT)) { // don't have perm to run this command
			return;
		}
		String[] arg = e.getArgument();
		if(arg.length == 0) {
			Messages.sendMessage(p, "not_forget_player");
			e.setCancelled(true); // command already handled here
			return;
		}
		Player target = Adapter.getAdapter().getPlayer(arg[0]);
		if (target == null) {
			if(arg.length == 1) { // not precise server
				Messages.sendMessage(p, "invalid_player", "%arg%", arg[0]);
			} else {
				p.sendMessage(ChatColor.GREEN + "Teleporting to server " + arg[1] + " ...");
				p.sendToServer(arg[1]);
			}
			e.setCancelled(true); // command already handled here
		} else if(!e.isProxy()) {
			InventoryManager.open(NegativityInventory.CHECK_MENU, p, target);
			e.setCancelled(true); // command already handled here
		}
	}

}
