package com.elikill58.negativity.common.commands;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;

public class LockChatCommand implements CommandListeners, Listeners {

	private boolean chatLocked = false;
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.CHAT_LOCK)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		if(chatLocked) {
			chatLocked = false;
			Messages.broadcastMessage("negativity.chat.unlocked", "%name%", sender.getName());
		} else {
			chatLocked = true;
			Messages.broadcastMessage("negativity.chat.locked", "%name%", sender.getName());
		}
		return true;
	}

	@EventListener
	public void onChat(PlayerChatEvent e) {
		if(chatLocked && !Perm.hasPerm(e.getPlayer(), Perm.CHAT_LOCK_BYPASS)) {
			e.setCancelled(true);
			Messages.sendMessage(e.getPlayer(), "negativity.chat.cant");
		}
	}
}
